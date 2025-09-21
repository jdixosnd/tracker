package com.babyfeed.tracker.data.repository

import com.babyfeed.tracker.data.local.Medication
import com.babyfeed.tracker.data.local.MedicationDao
import com.babyfeed.tracker.data.local.MedicationDose
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationRepository @Inject constructor(private val medicationDao: MedicationDao) {

    fun getActiveMedications(childId: Int): Flow<List<Medication>> {
        return medicationDao.getActiveMedications(childId)
    }

    fun getArchivedMedications(childId: Int): Flow<List<Medication>> {
        return medicationDao.getArchivedMedications(childId)
    }

    suspend fun insertOrUpdateMedication(medication: Medication) {
        medicationDao.insertOrUpdateMedication(medication)
    }

    suspend fun logDose(dose: MedicationDose) {
        medicationDao.insertDose(dose)
    }

    fun getDosesForMedication(medicationId: Int): Flow<List<MedicationDose>> {
        return medicationDao.getDosesForMedication(medicationId)
    }

    fun getLastDoseForMedication(medicationId: Int): Flow<MedicationDose?> {
        return medicationDao.getLastDoseForMedication(medicationId)
    }

    fun getMedication(id: Int): Flow<Medication?> {
        return medicationDao.getMedication(id)
    }

    fun getDoseHistoryForChild(childId: Int): Flow<List<com.babyfeed.tracker.data.local.DoseWithMedicationInfo>> {
        return medicationDao.getDoseHistoryForChild(childId)
    }

    fun getLastDoseForChild(childId: Int): Flow<com.babyfeed.tracker.data.local.LastDoseInfo?> {
        return medicationDao.getLastDoseForChild(childId)
    }

    fun getAllActiveMedications(): Flow<List<Medication>> {
        return medicationDao.getAllActiveMedications()
    }
}
