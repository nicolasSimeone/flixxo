package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sizes(
    var cover: String? = "",
    var hero: String? = "",
    var mainscreen: String? = "",
    var xcover: String? = ""
) : Parcelable