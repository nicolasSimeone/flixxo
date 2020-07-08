package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Category(@PrimaryKey var id: Int = 0, var name: String? = "", var thumb: String = "") : Parcelable