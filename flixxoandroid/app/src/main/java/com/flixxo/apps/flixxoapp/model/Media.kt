package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(
    val name: String? = "",
    val type: Long?,
    val url: String? = "",
    val order: Long?,
    val sizes: Sizes
) : Parcelable