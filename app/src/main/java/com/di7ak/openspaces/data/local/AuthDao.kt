package com.di7ak.openspaces.data.local

import androidx.room.*
import com.di7ak.openspaces.data.entities.AuthAttributes
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Query("SELECT * FROM sessions")
    fun getAllSessions() : Flow<List<AuthAttributes>>

    @Query("SELECT * FROM sessions WHERE userId = :userId")
    suspend fun getSession(userId: Int): AuthAttributes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: AuthAttributes)

    @Delete
    fun delete(userId: AuthAttributes)
}