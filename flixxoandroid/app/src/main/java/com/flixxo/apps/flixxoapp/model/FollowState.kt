package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FollowState(
    val user: Author,
    var state: Boolean
) : Parcelable