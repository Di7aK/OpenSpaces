package com.di7ak.openspaces.data.repository

import android.net.Uri
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.LentaEntity
import com.di7ak.openspaces.data.remote.BlogsDataSource
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.fromHtml
import com.di7ak.openspaces.utils.performGetOperation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlogsRepository @Inject constructor(
    private val remoteDataSource: BlogsDataSource,
    private val session: Session
) {

    fun fetch(user: String, page: Int) : Flow<Resource<LentaEntity>> {
        return performGetOperation(
            networkCall = {
                remoteDataSource.fetch(sid = session.current?.sid ?: "", page = page, user = user).apply {
                    data?.items?.forEach {
                        if(it.type == 0) {
                            val unescaped = it.bookmarkLink.fromHtml().toString()
                            val type = Uri.parse(unescaped).getQueryParameter("object_type")?.toInt() ?: 0
                            it.type = type
                        }
                    }
                }
            },
            saveCallResult = {}
        )
    }
}