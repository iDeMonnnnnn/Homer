package com.demon.apport.ui

import android.Manifest
import android.os.Build
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import com.demon.apport.App
import com.demon.apport.base.BaseActivity
import com.demon.apport.data.Constants
import com.demon.apport.databinding.ActivityChangePathBinding
import com.demon.apport.service.WebHelper
import com.demon.apport.util.get
import com.demon.apport.util.mmkv
import com.demon.apport.util.put
import com.demon.apport.util.toast
import com.demon.qfsolution.utils.getExternalOrFilesDirPath
import com.demon.qfsolution.utils.uriToFile
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
        changePath = it.uriToFile()?.absolutePath ?: ""
        binding.tvChange.text = "修改后的路径：$changePath"
    }


    override fun initData() {
        setToolbar("修改存储路径")

        val def: String = App.appContext.getExternalOrFilesDirPath(Environment.DIRECTORY_DCIM)
        val nowPath = "当前存储路径：${mmkv.get(Constants.MMKV_STORAGE_PATH, def)}"

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        binding.run {
            tvNow.text = nowPath

            btnChange.setOnClickListener {
                PermissionX.init(this@ChangePathActivity)
                    .permissions(*permissions)
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(deniedList, "修改存储路径需要文件存储、管理权限", "好的", "取消")
                    }
                    .request { allGranted, _, _ ->
                        if (!allGranted) {
                            "没有文件存储、管理权限~".toast()
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