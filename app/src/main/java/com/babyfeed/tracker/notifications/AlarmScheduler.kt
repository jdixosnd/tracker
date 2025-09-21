package com.babyfeed.tracker.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.babyfeed.tracker.data.local.Medication

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(medication: Medication, triggerAtMillis: Long) {
        val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
            putExtra("title", "Time for ${medication.name}")
            putExtra("message", "It's time to give ${medication.dosage} of ${medication.name}.")
            putExtra("medicationId", medication.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medication.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun cancel(medicationId: Int) {
        val intent = Intent(context, MedicationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
