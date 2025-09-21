package com.babyfeed.tracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Child::class, MilkFeed::class, Medication::class, MedicationDose::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun childDao(): ChildDao
    abstract fun milkFeedDao(): MilkFeedDao
    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "baby_feed_tracker_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
