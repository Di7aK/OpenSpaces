package com.di7ak.openspaces.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.di7ak.openspaces.data.entities.AuthAttributes

@Dao
interface AuthDao {
    @Query("SELECT * FROM sessions")
    fun getAllSessions() : LiveData<List<AuthAttributes>>

    @Query("SELECT * FROM sessions WHERE userId = :userId")
    suspend fun getSession(userId: Int): AuthAttributes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: AuthAttributes)

    @Delete
    fun delete(userId: AuthAttributes)
}