package com.demon.apport.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demon.apport.R
import com.demon.apport.data.InfoModel
import com.demon.apport.util.FileUtils
import com.demon.apport.util.loadImg
import com.demon.apport.util.toast
import com.demon.apport.util.visibleOrGone
import com.demon.qfsolution.utils.getExtensionByFileName
import com.demon.qfsolution.utils.getMimeTypeByFileName
import com.demon.qfsolution.utils.isImage
import com.demon.qfsolution.utils.isVideo

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
            holder.mCopy.visibleOrGone(infoModel.isTxt())
            if (infoModel.isApk()) {
                holder.ivIcon.setImageDrawable(infoModel.icon)
            } else {
                holder.ivIcon.setImageResource(FileUtils.getIconByPath(infoModel.path))
            }
            holder.mCopy.setOnClickListener {
                val content = FileUtils.readText(infoModel.path)
                if (content.isEmpty()) {
                    "文本内容为空！".toast()
                    return@setOnClickListener
                }
                try {
                    FileUtils.copyText(mContext, content)
                    "Copy success!".toast()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Copy failed!".toast()
                }

            }
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
        var mCopy: TextView
        var mDelete: TextView
        var mTvPath: TextView
        var ivIcon: ImageView

        init {
            mTvName = view.findViewById<View>(R.id.tv_name) as TextView
            mTvSize = view.findViewById<View>(R.id.tv_size) as TextView
            mOpen = view.findViewById<View>(R.id.tv_open) as TextView
            mCopy = view.findViewById<View>(R.id.tv_copy) as TextView
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