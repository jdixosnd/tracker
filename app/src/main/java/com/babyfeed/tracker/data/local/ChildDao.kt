package com.babyfeed.tracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Insert
    suspend fun insert(child: Child)

    @Update
    suspend fun update(child: Child)

    @Delete
    suspend fun delete(child: Child)

    @Query("SELECT * FROM children WHERE id = :id")
    fun getChild(id: Int): Flow<Child>

    @Query("SELECT * FROM children ORDER BY name ASC")
    fun getAllChildren(): Flow<List<Child>>
}
