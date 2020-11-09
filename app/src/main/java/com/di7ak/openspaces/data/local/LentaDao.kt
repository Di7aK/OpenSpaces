package com.di7ak.openspaces.data.local

import androidx.room.*
import com.di7ak.openspaces.ui.features.lenta.LentaModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LentaDao {
    @Query("SELECT * FROM lenta WHERE userId = :userId ORDER BY id DESC")
    fun getEvents(userId: Int) : Flow<List<LentaModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LentaModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LentaModel>)

    @Delete
    fun delete(item: LentaModel)
}