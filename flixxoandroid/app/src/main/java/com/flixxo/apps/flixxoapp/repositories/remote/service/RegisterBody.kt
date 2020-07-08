package com.flixxo.apps.flixxoapp.repositories.remote.service

data class RegisterBody(
    var nickname: String,
    var email: String,
    var password: String,
    var code: String? = "",
    var mobile: Boolean? = true,
    var mobileNumber: String? = ""
)