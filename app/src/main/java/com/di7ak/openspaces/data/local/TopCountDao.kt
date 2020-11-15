package com.di7ak.openspaces.data.local

import androidx.room.*
import com.di7ak.openspaces.data.entities.TopCountEntity

@Dao
interface TopCountDao {
    @Query("SELECT * FROM top_count WHERE userId = :userId LIMIT 1")
    fun getTopCount(userId: Int) : TopCountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TopCountEntity)

    @Delete
    fun delete(item: TopCountEntity)
}