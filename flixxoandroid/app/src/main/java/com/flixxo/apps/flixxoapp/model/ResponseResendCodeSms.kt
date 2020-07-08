package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseResendCodeSms(
    var messsage: String = "",
    var name: String = "",
    var showable: Boolean = false,
    var internal: Boolean = false,
    var freeAt: String = ""
) : Parcelable