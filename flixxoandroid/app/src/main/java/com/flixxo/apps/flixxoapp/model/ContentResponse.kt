package com.flixxo.apps.flixxoapp.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentResponse(
    var count: Int? = 0,
    var contents: List<Content>?,
    var series: List<Content>?
) : Parcelable {
    fun getContent(): List<Content>? {
        contents?.let {
            return it
        }

        series?.let {
            return it
        }

        return listOf()
    }
}