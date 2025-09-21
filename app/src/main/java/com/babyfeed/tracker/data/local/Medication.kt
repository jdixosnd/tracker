package com.babyfeed.tracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = Child::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Medication(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val childId: Int,
    val name: String,
    val dosage: String,
    val scheduleType: String,
    val scheduleDetails: String?,
    val startDate: Long,
    val endDate: Long?,
    val instructions: String?,
    val isActive: Boolean = true
)
