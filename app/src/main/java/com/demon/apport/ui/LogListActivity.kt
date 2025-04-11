package com.demon.apport.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demon.apport.R
import com.demon.apport.base.BaseActivity
import com.demon.apport.databinding.ActivityLogListBinding
import com.demon.apport.util.LogUtils
import java.io.File

/**
 * @author DeMonnnnnn
 * date 2022/8/12
 * email liu_demon@qq.com
 * desc
 */
class LogListActivity : BaseActivity<ActivityLogListBinding>() {

    private var list = mutableListOf<String>()

    private val adapter by lazy {
        LogAdapter()
    }

    override fun initData() {
        setToolbar("日志")
        list.addAll(LogUtils.getLogFiles())
        binding.rvData.adapter = adapter
    }


    inner class LogAdapter : RecyclerView.Adapter<LogHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogHolder {
            return LogHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false))
        }

        override fun onBindViewHolder(holder: LogHolder, position: Int) {
            val file = File(list[position])
            holder.tvText.text = file.name
            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, LogActivity::class.java)
                intent.putExtra("path", list[position])
                it.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = list.size
    }


    class LogHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        var tvText: TextView

        init {
            tvText = view.findViewById(R.id.tvText)
        }
    }
}