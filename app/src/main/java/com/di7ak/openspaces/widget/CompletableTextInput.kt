package com.di7ak.openspaces.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText

class CompletableTextInput constructor(context: Context, attrs: AttributeSet?) : TextInputEditText(context, attrs), CompletableView {
    override var isCompleted = MutableLiveData<Boolean>().apply {
        postValue(false)
    }
}