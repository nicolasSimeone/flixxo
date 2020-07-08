package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Author(
    val id: String? = "",
    var nickname: String? = "",
    @SerializedName("Profile") var profile: Profile? = Profile()
) : Parcelable

