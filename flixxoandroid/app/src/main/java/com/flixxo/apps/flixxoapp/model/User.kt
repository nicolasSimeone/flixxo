package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var email: String? = "",
    var nickname: String? = "",
    var status: Int?,
    var created: String? = "",
    var profile: Profile
) : Parcelable