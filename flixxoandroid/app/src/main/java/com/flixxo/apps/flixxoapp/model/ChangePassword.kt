package com.flixxo.apps.flixxoapp.model

data class ChangePassword(
    val currentPassword: String? = "",
    val newPassword: String? = ""
)