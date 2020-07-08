package com.flixxo.apps.flixxoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContentPurchased(
    val uuid: String = "",
    val userName: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}