package com.di7ak.openspaces.ui.features.main.web

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.di7ak.openspaces.data.Session

class WebViewModel  @ViewModelInject constructor(
    val session: Session
) : ViewModel() {

}