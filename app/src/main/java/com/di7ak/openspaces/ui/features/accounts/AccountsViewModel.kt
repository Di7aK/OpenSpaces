package com.di7ak.openspaces.ui.features.accounts

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.data.repository.AuthRepository

class AccountsViewModel @ViewModelInject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    val sessions = repository.getSessions()

    fun deleteSession(userId: AuthAttributes) {
        repository.deleteSession(userId)
    }
}