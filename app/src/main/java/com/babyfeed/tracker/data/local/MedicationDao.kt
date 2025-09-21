package com.babyfeed.tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class DoseWithMedicationInfo(
    val timestamp: Long,
    val name: String,
    val dosage: String
)

data class LastDoseInfo(
    val timestamp: Long,
    val name: String
)

@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMedication(medication: Medication)

    @Query("SELECT * FROM medications WHERE childId = :childId AND isActive = 1 ORDER BY name ASC")
    fun getActiveMedications(childId: Int): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE childId = :childId AND isActive = 0 ORDER BY name ASC")
    fun getArchivedMedications(childId: Int): Flow<List<Medication>>

    @Insert
    suspend fun insertDose(dose: MedicationDose)

    @Query("SELECT * FROM medication_doses WHERE medicationId = :medicationId ORDER BY timestamp DESC")
    fun getDosesForMedication(medicationId: Int): Flow<List<MedicationDose>>

    @Query("SELECT * FROM medication_doses WHERE medicationId = :medicationId ORDER BY timestamp DESC LIMIT 1")
    fun getLastDoseForMedication(medicationId: Int): Flow<MedicationDose?>

    @Query("SELECT * FROM medications WHERE id = :id")
    fun getMedication(id: Int): Flow<Medication?>

    @Query("""
        SELECT T1.timestamp, T2.name, T2.dosage
        FROM medication_doses AS T1
        INNER JOIN medications AS T2 ON T1.medicationId = T2.id
        WHERE T2.childId = :childId
        ORDER BY T1.timestamp DESC
    """)
    fun getDoseHistoryForChild(childId: Int): Flow<List<DoseWithMedicationInfo>>

    @Query("""
        SELECT T1.timestamp, T2.name
        FROM medication_doses AS T1
        INNER JOIN medications AS T2 ON T1.medicationId = T2.id
        WHERE T2.childId = :childId
        ORDER BY T1.timestamp DESC
        LIMIT 1
    """)
    fun getLastDoseForChild(childId: Int): Flow<LastDoseInfo?>

    @Query("SELECT * FROM medications WHERE isActive = 1")
    fun getAllActiveMedications(): Flow<List<Medication>>
}
