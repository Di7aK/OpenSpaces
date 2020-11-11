package com.di7ak.openspaces.data.local

import androidx.room.*
import com.di7ak.openspaces.data.entities.Attach

@Dao
interface AttachmentsDao {
    @Query("SELECT * FROM attachments WHERE parent = :parent")
    fun getAttachments(parent: Int) : List<Attach>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Attach)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Attach>)

    @Delete
    fun delete(item: Attach)
}