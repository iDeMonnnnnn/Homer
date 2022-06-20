package com.demon.apport.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demon.apport.R
import com.demon.apport.data.InfoModel
import com.demon.apport.util.FileUtils
import java.io.File

/**
 * @author DeMonnnnnn
 * @date 2022/6/20
 * @email liu_demon@qq.com
 * @desc
 */
class FilesAdapter constructor(val mApps: MutableList<InfoModel> = mutableListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val view = inflater.inflate(R.layout.empty_view, parent, false)
            EmptyViewHolder(view)
        } else {
            MyViewHolder(inflater.inflate(R.layout.layout_book_item, parent, false))
        }
    }

    var installAllowed = false
    override fun onBindViewHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        if (holder1 is MyViewHolder) {
            val holder = holder1
            val infoModel = mApps[position]
            holder.mTvAppName.text = infoModel.name + "(v" + infoModel.version + ")"

//            holder.mTvAppInstall.setText(infoModel.getName());
            holder.mTvAppSize.text = infoModel.size
            holder.mTvAppPath.text = infoModel.path
            holder.ivIcon.setImageDrawable(infoModel.icon)
            holder.mTvAppInstall.setOnClickListener(View.OnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    installAllowed = mContext.packageManager.canRequestPackageInstalls()
                    if (installAllowed) {
                        FileUtils.installApkFile(mContext, File(infoModel.path))
                    } else {
                        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${mContext.packageName}"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        mContext.startActivity(intent)
                        FileUtils.installApkFile(mContext, File(infoModel.path))
                        return@OnClickListener
                    }
                } else {
                    FileUtils.installApkFile(mContext, File(infoModel.path))
                }
            })
            holder.mTvAppDelete.setOnClickListener { FileUtils.delete(mContext, infoModel.packageName) }
            if (infoModel.isInstalled) {
                holder.mTvAppDelete.visibility = View.VISIBLE
            } else {
                holder.mTvAppDelete.visibility = View.GONE
            }
        }
    }

    internal inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemCount(): Int {
        return if (mApps.size > 0) mApps.size else 1
    }

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mTvAppName: TextView
        var mTvAppSize: TextView
        var mTvAppInstall: TextView
        var mTvAppDelete: TextView
        var mTvAppPath: TextView
        var ivIcon: ImageView

        init {
            mTvAppName = view.findViewById<View>(R.id.tv_name) as TextView
            mTvAppSize = view.findViewById<View>(R.id.tv_size) as TextView
            mTvAppInstall = view.findViewById<View>(R.id.tv_install) as TextView
            mTvAppPath = view.findViewById<View>(R.id.tv_path) as TextView
            mTvAppDelete = view.findViewById<View>(R.id.tv_delete) as TextView
            ivIcon = view.findViewById<View>(R.id.iv_icon) as ImageView
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mApps.size == 0) {
            1
        } else super.getItemViewType(position)
    }
}