package com.di7ak.openspaces.widget

import androidx.lifecycle.MutableLiveData

interface CompletableView {
    var isCompleted: MutableLiveData<Boolean>
}