package com.demon.apport.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.demon.apport.App
import com.demon.apport.base.BaseActivity
import com.demon.apport.data.Constants
import com.demon.apport.databinding.ActivityChangePathBinding
import com.demon.apport.service.WebHelper
import com.demon.apport.util.*
import com.demon.qfsolution.utils.getDataColumn
import com.demon.qfsolution.utils.getExternalOrFilesDirPath
import com.demon.qfsolution.utils.getFileFromUriN
import com.demon.qfsolution.utils.grantPermissions
import com.permissionx.guolindev.PermissionX
import java.io.File


/**
 * @author DeMonnnnnn
 * date 2022/8/12
 * email liu_demon@qq.com
 * desc
 */
class ChangePathActivity : BaseActivity<ActivityChangePathBinding>() {


    private var changePath = "${Environment.getExternalStorageDirectory().absolutePath}/Download"

    private val openDocumentTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
        it ?: return@registerForActivityResult
        val uri = Uri.parse(it.toString().replace("%3A", "/"))
        runCatching {
            val takeFlags = (intent.flags
                    and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            contentResolver.takePersistableUriPermission(uri, takeFlags)
            val documentFile = DocumentFile.fromTreeUri(this@ChangePathActivity, uri)
            changePath = uri.getDataColumn() ?: ""
            Log.i(TAG, "openDocumentTree: $changePath")
            binding.tvChange.text = "修改后的路径：$changePath"
        }.onFailure { e ->
            e.printStackTrace()
        }
    }


    override fun initData() {
        setToolbar("修改存储路径")

        val def: String = App.appContext.getExternalOrFilesDirPath(Environment.DIRECTORY_DCIM)
        val nowPath = "当前存储路径：${mmkv.get(Constants.MMKV_STORAGE_PATH, def)}"

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        binding.run {
            tvNow.text = nowPath

            btnChange.setOnClickListener {
                PermissionX.init(this@ChangePathActivity)
                    .permissions(*permissions)
                    .request { allGranted, _, _ ->
                        if (!allGranted) {
                            "没有存储权限~".toast()
                        } else {
                            openDocumentTree.launch(null)
                        }
                    }
            }

            btnSave.setOnClickListener {
                if (changePath.isEmpty()) {
                    "修改路径失败！".toast()
                    return@setOnClickListener
                }
                mmkv.put(Constants.MMKV_STORAGE_PATH, changePath)
                WebHelper.instance.dir = File(changePath)
                finish()
            }
        }
    }
}