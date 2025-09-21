package com.babyfeed.tracker.ui.screens.medication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfeed.tracker.data.local.Medication
import com.babyfeed.tracker.data.local.MedicationDose
import com.babyfeed.tracker.data.repository.MedicationRepository
import com.babyfeed.tracker.notifications.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val alarmScheduler: AlarmScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val childId: Flow<Int> = savedStateHandle.getStateFlow("childId", -1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMedications: Flow<List<Medication>> = childId.flatMapLatest { id ->
        if (id != -1) medicationRepository.getActiveMedications(id) else flowOf(emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val archivedMedications: Flow<List<Medication>> = childId.flatMapLatest { id ->
        if (id != -1) medicationRepository.getArchivedMedications(id) else flowOf(emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val doseHistory = childId.flatMapLatest { id ->
        if (id != -1) medicationRepository.getDoseHistoryForChild(id) else flowOf(emptyList())
    }

    fun getLastDose(medicationId: Int): Flow<MedicationDose?> {
        return medicationRepository.getLastDoseForMedication(medicationId)
    }

    fun getMedication(id: Int): Flow<Medication?> {
        return medicationRepository.getMedication(id)
    }

    fun addOrUpdateMedication(medication: Medication) {
        viewModelScope.launch {
            medicationRepository.insertOrUpdateMedication(medication)
            if (medication.isActive && medication.scheduleType != "AS_NEEDED") {
                calculateAndScheduleNextAlarm(medication)
            } else {
                alarmScheduler.cancel(medication.id)
            }
        }
    }

    fun logDose(medicationId: Int) {
        viewModelScope.launch {
            medicationRepository.logDose(MedicationDose(medicationId = medicationId, timestamp = System.currentTimeMillis()))
            val medication = medicationRepository.getMedication(medicationId).first()
            medication?.let {
                if (it.isActive && it.scheduleType != "AS_NEEDED") {
                    calculateAndScheduleNextAlarm(it)
                }
            }
        }
    }

    fun archiveMedication(medication: Medication) {
        viewModelScope.launch {
            medicationRepository.insertOrUpdateMedication(medication.copy(isActive = false))
            alarmScheduler.cancel(medication.id)
        }
    }

    private suspend fun calculateAndScheduleNextAlarm(medication: Medication) {
        val nextAlarmTime = when (medication.scheduleType) {
            "EVERY_X_HOURS" -> {
                val hours = medication.scheduleDetails?.toIntOrNull() ?: return
                val lastDose = medicationRepository.getLastDoseForMedication(medication.id).first()
                val lastDoseTime = lastDose?.timestamp ?: medication.startDate
                lastDoseTime + hours * 60 * 60 * 1000
            }
            else -> null
        }

        nextAlarmTime?.let {
            if (it > System.currentTimeMillis()) {
                alarmScheduler.schedule(medication, it)
            }
        }
    }
}
