package com.flixxo.apps.flixxoapp.model

data class ResetPasswordBody(
    var email: String,
    var code: String,
    var password: String
)