package com.flixxo.apps.flixxoapp.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterResponse(
    var success: Boolean = false,
    var secret: String = ""
) : Parcelable