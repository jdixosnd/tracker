package com.babyfeed.tracker.data.repository

import com.babyfeed.tracker.data.local.Child
import com.babyfeed.tracker.data.local.ChildDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildRepository @Inject constructor(private val childDao: ChildDao) {

    fun getAllChildren(): Flow<List<Child>> {
        return childDao.getAllChildren()
    }

    fun getChild(id: Int): Flow<Child> {
        return childDao.getChild(id)
    }

    suspend fun insertChild(child: Child) {
        childDao.insert(child)
    }

    suspend fun updateChild(child: Child) {
        childDao.update(child)
    }

    suspend fun deleteChild(child: Child) {
        childDao.delete(child)
    }
}
