package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.data.local.AuthDao
import com.di7ak.openspaces.data.remote.AuthDataSource
import com.di7ak.openspaces.utils.ioThread
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: AuthDataSource,
    private val localDataSource: AuthDao
) {

    fun login(login: String, password: String, code: String) = performGetOperation(
        networkCall = { remoteDataSource.login(login = login, password = password, code = code) },
        saveCallResult = {
            if(it.attributes != null) localDataSource.insert(it.attributes!!)
        }
    )

    fun check(sid: String) = performGetOperation(
        networkCall = { remoteDataSource.check(sid = sid) },
        saveCallResult = {
            if(it.attributes != null) localDataSource.insert(it.attributes!!)
        }
    )

    fun getSessions() =  localDataSource.getAllSessions()
    suspend fun getSession(userId: Int) =  localDataSource.getSession(userId = userId)

    fun deleteSession(userId: AuthAttributes) = ioThread { localDataSource.delete(userId) }
}