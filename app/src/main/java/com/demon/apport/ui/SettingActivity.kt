package com.demon.apport.ui

import android.content.Intent
import com.demon.apport.base.BaseActivity
import com.demon.apport.databinding.ActivitySettingBinding
import com.demon.apport.util.LogUtils

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    
    override fun initData() {
        setToolbar("设置")


        binding.run {
            tvPath.setOnClickListener {
                val intent = Intent(this@SettingActivity, LogListActivity::class.java)
                startActivity(intent)
            }
            tvLog.setOnClickListener {
                val intent = Intent(this@SettingActivity, LogListActivity::class.java)
                startActivity(intent)
            }
        }
    }
}