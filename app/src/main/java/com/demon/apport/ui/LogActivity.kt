package com.demon.apport.ui

import android.util.Log
import com.demon.apport.base.BaseActivity
import com.demon.apport.databinding.ActivityLogBinding
import com.demon.apport.util.FileUtils
import java.io.File

/**
 * @author DeMon
 * Created on 2022/7/11.
 * E-mail idemon_liu@qq.com
 * Desc:
 */
class LogActivity : BaseActivity<ActivityLogBinding>() {


    override fun initData() {

        val path = intent.getStringExtra("path")
        setToolbar(File(path).name)
        Log.i(TAG, "initData: $path")
        binding.tvContent.text = FileUtils.readText(path)
    }
}