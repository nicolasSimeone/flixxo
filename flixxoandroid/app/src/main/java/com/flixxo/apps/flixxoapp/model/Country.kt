package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Country(
    var id: Int,
    @SerializedName("Name") var name: CountryName
) : Parcelable