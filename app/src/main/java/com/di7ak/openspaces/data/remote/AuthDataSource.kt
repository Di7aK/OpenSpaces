package com.di7ak.openspaces.data.remote

import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val authService: AuthService
): BaseDataSource() {

    suspend fun login(login: String, password: String, code: String) = getResult {
        authService.login(
            login = login,
            password = password,
            code = code)
    }

    suspend fun check(sid: String) = getResult {
        authService.check(
            sid = sid)
    }
}