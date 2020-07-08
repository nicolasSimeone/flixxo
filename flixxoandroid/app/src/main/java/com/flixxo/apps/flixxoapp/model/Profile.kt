package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    var userId: Int? = 0,
    var realName: String? = "",
    var birthDate: String? = "",
    var lang: String? = "",
    var gender: String? = "",
    var countryId: Int? = 0,
    var avatar: String? = ""
) : Parcelable