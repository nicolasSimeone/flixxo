package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdWatchedResponse(
    var success: Boolean?,
    var newBalance: String?,
    var rewards: Double?
) : Parcelable

