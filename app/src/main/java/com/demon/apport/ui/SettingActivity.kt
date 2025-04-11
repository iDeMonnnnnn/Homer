package com.demon.apport.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import com.base.log.Log
import com.demon.apport.App
import com.demon.apport.base.BaseActivity
import com.demon.apport.data.Constants
import com.demon.apport.databinding.ActivitySettingBinding
import com.demon.apport.util.FileUtils
import com.demon.apport.util.get
import com.demon.apport.util.mmkv
import com.demon.qfsolution.utils.getExternalOrFilesDirPath

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    override fun initData() {
        setToolbar("设置")


        binding.run {
            tvPath.setOnClickListener {
                val intent = Intent(this@SettingActivity, ChangePathActivity::class.java)
                startActivity(intent)
            }
            tvDelete.setOnClickListener {
                AlertDialog.Builder(this@SettingActivity).setTitle("警告").setMessage("这样会清空当前存储路径内全部已传输的文件，是否继续？")
                    .setPositiveButton("确认", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            Log.i(TAG, "setPositiveButton: 确认")
                            val def: String = App.appContext.getExternalOrFilesDirPath(Environment.DIRECTORY_DCIM)
                            FileUtils.deleteAll(mmkv.get(Constants.MMKV_STORAGE_PATH, def))
                            dialog?.dismiss()
                        }
                    }).setNegativeButton("取消", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            Log.i(TAG, "setNegativeButton: 取消")
                            dialog?.dismiss()
                        }
                    }).show()
            }
            tvLog.setOnClickListener {
                val intent = Intent(this@SettingActivity, LogListActivity::class.java)
                startActivity(intent)
            }
        }
    }
}