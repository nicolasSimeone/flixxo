package com.flixxo.apps.flixxoapp.model


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    var success: Boolean = false,

    @SerializedName("secret")
    var secret: String = "",

    @SerializedName("isModerator")
    var isModerator: Boolean = false
)