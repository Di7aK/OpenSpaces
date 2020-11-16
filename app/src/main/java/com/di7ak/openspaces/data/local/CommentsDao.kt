package com.di7ak.openspaces.data.local

import androidx.room.*
import com.di7ak.openspaces.data.entities.CommentItemEntity

@Dao
interface CommentsDao {
    @Query("SELECT * FROM comments WHERE parent = :postId ORDER BY id ASC")
    fun getComments(postId: Int) : List<CommentItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CommentItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CommentItemEntity>)

    @Delete
    fun delete(item: CommentItemEntity)

    @Query("DELETE FROM comments WHERE id = :itemId")
    fun delete(itemId: Int)
}