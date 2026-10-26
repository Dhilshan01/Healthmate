package com.example.healthmate.core.notifications

import android.app.*
import android.content.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.healthmate.R
import java.util.*

object ReminderScheduler {
    const val CHANNEL = "wellness_reminders"
    fun schedule(context: Context, id: Int, minutes: Int, title: String, body: String) {
        val intent = Intent(context, ReminderReceiver::class.java).putExtra("id", id).putExtra("minutes", minutes).putExtra("title", title).putExtra("body", body)
        val pending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val time = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, minutes / 60); set(Calendar.MINUTE, minutes % 60); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0); if (before(Calendar.getInstance())) add(Calendar.DATE, 1) }.timeInMillis
        (context.getSystemService(AlarmManager::class.java)).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    fun cancel(context: Context, id: Int) { val pending = PendingIntent.getBroadcast(context, id, Intent(context, ReminderReceiver::class.java), PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE); if (pending != null) context.getSystemService(AlarmManager::class.java).cancel(pending) }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(ReminderScheduler.CHANNEL, "Wellness reminders", NotificationManager.IMPORTANCE_DEFAULT))
        val id = intent.getIntExtra("id", 0); val title = intent.getStringExtra("title") ?: "HealthMate"; val body = intent.getStringExtra("body") ?: "Time for a healthy habit."
        NotificationManagerCompat.from(context).notify(id, NotificationCompat.Builder(context, ReminderScheduler.CHANNEL).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(body).setAutoCancel(true).build())
        ReminderScheduler.schedule(context, id, intent.getIntExtra("minutes", 600), title, body)
    }
}
