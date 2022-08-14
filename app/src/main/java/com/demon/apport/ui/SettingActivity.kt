package com.demon.apport.ui

import android.content.Intent
import android.os.Environment
import com.demon.apport.App
import com.demon.apport.base.BaseActivity
import com.demon.apport.data.Constants
import com.demon.apport.databinding.ActivitySettingBinding
import com.demon.apport.util.FileUtils
import com.demon.apport.util.LogUtils
import com.demon.apport.util.get
import com.demon.apport.util.mmkv
import com.demon.qfsolution.utils.getExternalOrFilesDirPath
import java.io.File

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    override fun initData() {
        setToolbar("设置")


        binding.run {
            tvPath.setOnClickListener {
                val intent = Intent(this@SettingActivity, ChangePathActivity::class.java)
                startActivity(intent)
            }
            tvDelete.setOnClickListener {
                val def: String = App.appContext.getExternalOrFilesDirPath(Environment.DIRECTORY_DCIM)
                FileUtils.deleteAll(mmkv.get(Constants.MMKV_STORAGE_PATH, def))
            }
            tvLog.setOnClickListener {
                val intent = Intent(this@SettingActivity, LogListActivity::class.java)
                startActivity(intent)
            }
        }
    }
}