package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.data.entities.AuthEntity
import com.di7ak.openspaces.data.entities.AuthRequest
import com.di7ak.openspaces.data.local.AuthDao
import com.di7ak.openspaces.data.remote.AuthDataSource
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.ioThread
import com.di7ak.openspaces.utils.performGetOperation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: AuthDataSource,
    private val localDataSource: AuthDao
) {

    /*
    suspend fun login(login: String, password: String): Resource<AuthEntity> {
        val result = remoteDataSource.login(
            login = login,
            password = password
        )

        return if(result.status == Resource.Status.SUCCESS) {
            Resource.success(result.data!!)
        } else {
            Resource.error(result.message!!)
        }
    }*/

    fun login(login: String, password: String, code: String) = performGetOperation(
        networkCall = { remoteDataSource.login(login = login, password = password, code = code) },
        saveCallResult = {
            if(it.attributes != null) localDataSource.insert(it.attributes)
        }
    )

    fun check(sid: String) = performGetOperation(
        networkCall = { remoteDataSource.check(sid = sid) },
        saveCallResult = {
            if(it.attributes != null) localDataSource.insert(it.attributes)
        }
    )

    fun getSessions() =  localDataSource.getAllSessions()
    fun getSession(userId: Int) =  localDataSource.getSession(userId = userId)

    fun deleteSession(userId: AuthAttributes) = ioThread { localDataSource.delete(userId) }
}