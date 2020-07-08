package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryName(
    var value: String = ""
) : Parcelable