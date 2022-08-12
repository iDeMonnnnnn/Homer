package com.demon.apport.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demon.apport.R
import com.demon.apport.data.InfoModel
import com.demon.apport.util.FileUtils

/**
 * @author DeMonnnnnn
 * @date 2022/6/20
 * @email liu_demon@qq.com
 * @desc
 */
class FilesAdapter constructor(val mList: MutableList<InfoModel> = mutableListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "FilesAdapter"
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            -1 -> EmptyViewHolder(inflater.inflate(R.layout.empty_view, parent, false))
            else -> FileViewHolder(inflater.inflate(R.layout.item_file, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FileViewHolder) {
            val infoModel = mList[position]
            holder.mTvName.text = if (infoModel.isApk()) {
                infoModel.name + "(v" + infoModel.version + ")"
            } else {
                infoModel.name
            }
            holder.mOpen.text = if (infoModel.isApk()) {
                "安装"
            } else {
                "打开"
            }
            holder.mTvSize.text = infoModel.size
            holder.mTvPath.text = infoModel.path
            holder.ivIcon.setImageDrawable(infoModel.icon)
            holder.mOpen.setOnClickListener {
                FileUtils.openFileorAPk(mContext, infoModel)
            }
            holder.mDelete.setOnClickListener { FileUtils.delete(infoModel.path) }
        }
    }


    override fun getItemCount(): Int {
        return if (mList.size > 0) mList.size else 1
    }

    internal inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mTvName: TextView
        var mTvSize: TextView
        var mOpen: TextView
        var mDelete: TextView
        var mTvPath: TextView
        var ivIcon: ImageView

        init {
            mTvName = view.findViewById<View>(R.id.tv_name) as TextView
            mTvSize = view.findViewById<View>(R.id.tv_size) as TextView
            mOpen = view.findViewById<View>(R.id.tv_open) as TextView
            mTvPath = view.findViewById<View>(R.id.tv_path) as TextView
            mDelete = view.findViewById<View>(R.id.tv_delete) as TextView
            ivIcon = view.findViewById<View>(R.id.iv_icon) as ImageView
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.size == 0) {
            -1
        } else {
            0
        }
    }
}