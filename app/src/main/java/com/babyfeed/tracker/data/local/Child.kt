package com.babyfeed.tracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dob: Long, // Date of Birth as timestamp
    val gender: String?,
    val profilePhotoUri: String?
)
