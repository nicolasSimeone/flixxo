package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Language(
    @PrimaryKey val lang: String = "",
    val name: String? = "",
    val nameEn: String? = "",
    val nameNative: String? = ""
) : Parcelable