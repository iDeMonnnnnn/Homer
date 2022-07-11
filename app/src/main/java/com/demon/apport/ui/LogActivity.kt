package com.demon.apport.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demon.apport.databinding.ActivityLogBinding
import com.demon.apport.util.FileUtils

/**
 * @author DeMon
 * Created on 2022/7/11.
 * E-mail idemon_liu@qq.com
 * Desc:
 */
class LogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val path = intent.getStringExtra("path")

        binding.tvContent.text = FileUtils.readText(path)

    }
}