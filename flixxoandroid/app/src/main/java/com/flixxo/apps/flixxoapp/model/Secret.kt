package com.flixxo.apps.flixxoapp.model

import android.util.Base64

class Secret(tokenString: String) {
    val uuid: String
    val secret: ByteArray
    val secretBase64: String

    init {
        val parts = tokenString.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size != 2) {
            throw IllegalArgumentException("The token is wrong")
        }

        this.uuid = parts[0]
        this.secretBase64 = parts[1]
        this.secret = Base64.decode(this.secretBase64, Base64.DEFAULT)
    }
}