package com.babyfeed.tracker.di

import android.content.Context
import com.babyfeed.tracker.data.local.AppDatabase
import com.babyfeed.tracker.data.local.ChildDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import com.babyfeed.tracker.notifications.AlarmScheduler
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideChildDao(appDatabase: AppDatabase): ChildDao {
        return appDatabase.childDao()
    }

    @Provides
    @Singleton
    fun provideMilkFeedDao(appDatabase: AppDatabase): com.babyfeed.tracker.data.local.MilkFeedDao {
        return appDatabase.milkFeedDao()
    }

    @Provides
    @Singleton
    fun provideMedicationDao(appDatabase: AppDatabase): com.babyfeed.tracker.data.local.MedicationDao {
        return appDatabase.medicationDao()
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmScheduler(context)
    }
}
