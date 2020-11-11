package com.di7ak.openspaces.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <T, A> performGetOperation(databaseQuery: () -> T,
                               networkCall: suspend () -> Resource<A>,
                               saveCallResult: suspend (A) -> Unit): Flow<Resource<T>> =
    flow {
        emit(Resource.loading())

        emit(Resource.success(databaseQuery.invoke()))

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == Resource.Status.SUCCESS) {
            saveCallResult(responseStatus.data!!)
            emit(Resource.success(databaseQuery.invoke()))
        } else if (responseStatus.status == Resource.Status.ERROR) {
            emit(Resource.error(responseStatus.message!!))
        }
    }.flowOn(Dispatchers.IO)

fun <T> performGetOperation(networkCall: suspend () -> Resource<T>,
                            saveCallResult: suspend (T) -> Unit): Flow<Resource<T>> =
    flow {
        emit(Resource.loading())

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == Resource.Status.SUCCESS) {
            emit(Resource.success(responseStatus.data!!))
            saveCallResult(responseStatus.data)
        } else if (responseStatus.status == Resource.Status.ERROR) {
            emit(Resource.error(responseStatus.message!!))
        }
    }.flowOn(Dispatchers.IO)