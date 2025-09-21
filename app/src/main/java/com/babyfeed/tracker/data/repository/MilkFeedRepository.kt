package com.babyfeed.tracker.data.repository

import com.babyfeed.tracker.data.local.MilkFeed
import com.babyfeed.tracker.data.local.MilkFeedDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilkFeedRepository @Inject constructor(private val milkFeedDao: MilkFeedDao) {

    fun getFeedsForChild(childId: Int): Flow<List<MilkFeed>> {
        return milkFeedDao.getFeedsForChild(childId)
    }

    fun getTotalConsumedSince(childId: Int, startTimestamp: Long): Flow<Int?> {
        return milkFeedDao.getTotalConsumedSince(childId, startTimestamp)
    }

    fun getFeedsBetween(childId: Int, startTimestamp: Long, endTimestamp: Long): Flow<List<MilkFeed>> {
        return milkFeedDao.getFeedsBetween(childId, startTimestamp, endTimestamp)
    }

    suspend fun insertFeed(milkFeed: MilkFeed) {
        milkFeedDao.insert(milkFeed)
    }
}
