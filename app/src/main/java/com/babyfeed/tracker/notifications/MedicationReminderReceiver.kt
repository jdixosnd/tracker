package com.babyfeed.tracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MedicationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Medication Reminder"
        val message = intent.getStringExtra("message") ?: "It's time for medication."

        val notificationHelper = NotificationHelper(context)
        notificationHelper.createNotificationChannel() // It's safe to call this multiple times
        notificationHelper.showNotification(title, message)
    }
}
