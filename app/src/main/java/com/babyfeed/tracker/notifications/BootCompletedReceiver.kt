package com.babyfeed.tracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.babyfeed.tracker.data.local.Medication
import com.babyfeed.tracker.data.repository.MedicationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var medicationRepository: MedicationRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val medications = medicationRepository.getAllActiveMedications().first()
                medications.forEach { medication ->
                    if (medication.scheduleType != "AS_NEEDED") {
                        val nextAlarmTime = calculateNextAlarmTime(medication)
                        nextAlarmTime?.let {
                            if (it > System.currentTimeMillis()) {
                                alarmScheduler.schedule(medication, it)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun calculateNextAlarmTime(medication: Medication): Long? {
        return when (medication.scheduleType) {
            "EVERY_X_HOURS" -> {
                val hours = medication.scheduleDetails?.toIntOrNull() ?: return null
                val lastDose = medicationRepository.getLastDoseForMedication(medication.id).first()
                val lastDoseTime = lastDose?.timestamp ?: medication.startDate
                lastDoseTime + hours * 60 * 60 * 1000
            }
            else -> null
        }
    }
}
