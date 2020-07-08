package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdvertisementResponse(
    val id: Int?,
    val title: String? = "",
    val url: String? = ""
) : Parcelable
