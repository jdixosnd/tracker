package com.babyfeed.tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkFeedDao {
    @Insert
    suspend fun insert(milkFeed: MilkFeed)

    @Query("SELECT * FROM milk_feeds WHERE childId = :childId ORDER BY timestamp DESC")
    fun getFeedsForChild(childId: Int): Flow<List<MilkFeed>>

    @Query("SELECT SUM(amountConsumed) FROM milk_feeds WHERE childId = :childId AND timestamp >= :startTimestamp")
    fun getTotalConsumedSince(childId: Int, startTimestamp: Long): Flow<Int?>

    @Query("SELECT * FROM milk_feeds WHERE childId = :childId AND timestamp >= :startTimestamp AND timestamp < :endTimestamp")
    fun getFeedsBetween(childId: Int, startTimestamp: Long, endTimestamp: Long): Flow<List<MilkFeed>>
}
