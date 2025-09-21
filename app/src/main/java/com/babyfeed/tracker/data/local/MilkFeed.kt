package com.babyfeed.tracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "milk_feeds",
    foreignKeys = [
        ForeignKey(
            entity = Child::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MilkFeed(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val childId: Int,
    val timestamp: Long,
    val amountOffered: Int,
    val amountConsumed: Int,
    val notes: String?
)
