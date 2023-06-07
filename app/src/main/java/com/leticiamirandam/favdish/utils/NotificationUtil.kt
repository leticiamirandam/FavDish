package com.leticiamirandam.favdish.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.presentation.MainActivity
import java.text.SimpleDateFormat
import java.util.*


object NotificationUtil {
    private const val CHANNEL_ID_TIMER = "menu_timer"
    private const val CHANNEL_NAME_TIMER = "App Timer"
    private const val TIMER_ID = 0
    private var MAX_PROGRESS: Int = 100
    lateinit var ringtone: Ringtone

    @RequiresApi(Build.VERSION_CODES.O)
    fun showTimerExpired(context: Context) {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()

        val stopAlarmIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        stopAlarmIntent.action = Constants.ACTION_STOP_ALARM
        val startPendingIntent = getPendingIntentBroadcast(context, stopAlarmIntent)

        val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER)
        notificationBuilder.setContentTitle("Timer expired!")
            .setProgress(MAX_PROGRESS, MAX_PROGRESS, false)
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .addAction(R.drawable.ic_play_arrow, "Turn off alarm", startPendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER)
        notificationManager.notify(TIMER_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showTimerRunning(context: Context, wakeUpTime: Long) {
        val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        stopIntent.action = Constants.ACTION_STOP
        val stopPendingIntent = getPendingIntentBroadcast(context, stopIntent)

        val dateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

        val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER)
        notificationBuilder.setContentTitle("Timer is running.")
            .setContentText("End: ${dateFormat.format(Date(wakeUpTime))}")
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .setOngoing(true)
            .setProgress(MAX_PROGRESS, 30, false)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER)
        notificationManager.notify(TIMER_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showTimerPaused(context: Context) {
        val resumeIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        resumeIntent.action = Constants.ACTION_RESUME
        val resumePendingIntent = getPendingIntentBroadcast(context, resumeIntent)

        val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER)
        notificationBuilder.setContentTitle("Timer is paused.")
            .setContentText("Resume?")
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .setOngoing(true)
            .addAction(R.drawable.ic_play_arrow, "Resume", resumePendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER)
        notificationManager.notify(TIMER_ID, notificationBuilder.build())
    }

    fun hideTimerNotification(context: Context) {
        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.cancel(TIMER_ID)
    }

    private fun getBasicNotificationBuilder(
        context: Context,
        channelId: String,
    ): NotificationCompat.Builder {

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_timer)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setDefaults(0)
            .setSilent(true)
            .setSound(null)
    }

    private fun getPendingIntentBroadcast(context: Context, intent: Intent): PendingIntent {
        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_MUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getBroadcast(
            context,
            0, intent,
            pendingIntentFlag
        )
    }

    private fun <T> getPendingIntentWithStack(
        context: Context,
        javaClass: Class<T>
    ): PendingIntent {
        val resultIntent = Intent(context, javaClass)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(javaClass)
        stackBuilder.addNextIntent(resultIntent)

        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_MUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
        return stackBuilder.getPendingIntent(0, pendingIntentFlag)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun NotificationManager.createNotificationChannel(
        channelID: String,
        channelName: String,
    ) {
        val notificationChannel =
            NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.setSound(null, null)
        this.createNotificationChannel(notificationChannel)
    }
}