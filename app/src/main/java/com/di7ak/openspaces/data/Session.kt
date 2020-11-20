package com.di7ak.openspaces.data

import android.content.Context
import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.data.local.AuthDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Session(private val authDao: AuthDao, context: Context) {
    companion object {
        private const val PREFS_NAME = "session"
        private const val USER_ID = "user_id"
    }
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    var current: AuthAttributes? = null
    set(value) {
        field = value
        prefs.edit().putInt(USER_ID, value?.userId ?: 0).apply()
    }

    init {
        val currentUserId = prefs.getInt(USER_ID, 0)
        CoroutineScope(Dispatchers.IO).launch {
            current = authDao.getSession(currentUserId)
        }
    }
}