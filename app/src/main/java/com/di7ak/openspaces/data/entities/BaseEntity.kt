package com.di7ak.openspaces.data.entities

import com.google.gson.annotations.SerializedName

open class BaseEntity (
    @SerializedName("code") val code: Int  = 0,
    @SerializedName("captcha_url") val captchaUrl: String  = ""
)