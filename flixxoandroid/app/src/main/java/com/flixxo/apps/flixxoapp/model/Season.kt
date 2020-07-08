package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Season(
    val uuid: String? = "",
    val title: String? = "",
    val number: Int?,
    val content: List<Content>
) : Parcelable