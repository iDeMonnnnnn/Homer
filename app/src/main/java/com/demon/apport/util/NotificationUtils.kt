package com.demon.apport.util

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import com.demon.apport.App
import com.demon.apport.R

/**
 * @author DeMonnnnnn
 * date 2022/6/29
 * email liu_demon@qq.com
 * desc
 */
object NotificationUtils {

    const val ALIVE_ID = "alive_id"

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationByChannel(context: Context): Notification {
        val launchIntentForPackage = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pIntent = PendingIntent.getActivity(context, 0, launchIntentForPackage, PendingIntent.FLAG_UPDATE_CURRENT)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 用户可以看到的通知渠道的名字.
        val name: CharSequence = context.getString(R.string.app_name)
        // 用户可以看到的通知渠道的描述
        val description = context.getString(R.string.app_name) + context.getString(R.string.notification_desc)
        // 在notificationManager中创建该通知渠道
        // 仅需要常驻通知栏，所以级别是low
        val channel: NotificationChannel = createLowNotificationChannel(ALIVE_ID, "运行服务", description)
        mNotificationManager.createNotificationChannel(channel)
        return Notification.Builder(context, ALIVE_ID)
            //.setContentTitle(name)
            //.setTicker(name)
            .setContentText(description)
            .setSmallIcon(R.mipmap.icon_logo)
            .setContentIntent(pIntent)
            .setOngoing(true)
            .build()
    }

    /**
     * 创建渠道
     *
     * @param id          渠道id
     * @param name        渠道名
     * @param description 渠道属性
     * @param importance  通知级别
     * @return NotificationChannel
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(id: String, name: String, description: String, importance: Int): NotificationChannel {
        val mChannel = NotificationChannel(id, name, importance)
        mChannel.description = description
        return mChannel
    }

    /**
     * 删除指定渠道
     *
     * @param channelId
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun deleteChannel(channelId: String) {
        val manager = App.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.deleteNotificationChannel(channelId)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createDefaultNotificationChannel(id: String, name: String, description: String): NotificationChannel? {
        val channel: NotificationChannel = createNotificationChannel(id, name, description, NotificationManager.IMPORTANCE_DEFAULT)
        // 不显示角标
        channel.setShowBadge(false)
        // 8.0以后Notification设置声音是无效的，只能通过渠道设置
        // 为了兼容8.0以前，Notification和渠道应同时设置
        channel.enableVibration(true)
        channel.enableLights(true)
        //自定义提示音
/*        channel.setSound(
            Uri.parse("android.resource://" +  App.appContext.packageName.toString() + "/" + R.raw.sound_default),
            Notification.AUDIO_ATTRIBUTES_DEFAULT
        )*/
        return channel
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createLowNotificationChannel(id: String, name: String, description: String): NotificationChannel {
        val channel: NotificationChannel = createNotificationChannel(id, name, description, NotificationManager.IMPORTANCE_LOW)
        // 不发出系统提示音，本身收到消息会有声音
        channel.setSound(null, null)
        // 不显示角标
        channel.setShowBadge(false)
        // 不提示灯
        channel.enableLights(false)
        // 不震动
        channel.enableVibration(false)
        return channel
    }


    fun buildNotification(context: Context): Notification {
        // 直接打开app
        val launchIntentForPackage = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pIntent = PendingIntent.getActivity(context, 0, launchIntentForPackage, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = Notification.Builder(context)
        builder.setSmallIcon(R.mipmap.icon_logo)
        //builder.setTicker(context.getString(R.string.app_name))
        //builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(context.getString(R.string.app_name) + context.getString(R.string.notification_desc))
        builder.setContentIntent(pIntent)
        builder.setOngoing(true)
        return builder.build()
    }
}