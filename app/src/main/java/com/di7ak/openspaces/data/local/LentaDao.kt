package com.di7ak.openspaces.data.local

import androidx.room.*
import com.di7ak.openspaces.data.entities.LentaItemEntity

@Dao
interface LentaDao {
    @Query("SELECT * FROM lenta WHERE userId = :userId ORDER BY date DESC")
    fun getEvents(userId: Int) : List<LentaItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LentaItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LentaItemEntity>)

    @Delete
    fun delete(item: LentaItemEntity)
}