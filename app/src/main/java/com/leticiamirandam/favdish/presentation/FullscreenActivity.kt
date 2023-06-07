package com.leticiamirandam.favdish.presentation

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.leticiamirandam.favdish.R
import com.leticiamirandam.favdish.utils.TimerExpiredReceiver

class FullscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_notification)
        createNotificationChannel(this)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun buttonClick(view: View?) {
        val intent = Intent(FULL_SCREEN_ACTION, null, this, TimerExpiredReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager: AlarmManager =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 15000,
            pendingIntent
        )
        NotificationManagerCompat.from(this)
            .cancel(0)
    }

    companion object {
        private val CHANNEL_ID = "menu_timer"
        val FULL_SCREEN_ACTION = "full_screen_action"

        fun CreateFullScreenNotification(context: Context) {
            val intent = Intent(context, FullscreenActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Full Screen Alarm Test")
                .setContentText("This is a test")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
            NotificationManagerCompat.from(context).notify(0, notificationBuilder.build())
        }

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = NotificationManagerCompat.from(context)
                if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        "App Timer",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    channel.description = "channel_description"
                    notificationManager.createNotificationChannel(channel)
                }
            }
        }
    }
}