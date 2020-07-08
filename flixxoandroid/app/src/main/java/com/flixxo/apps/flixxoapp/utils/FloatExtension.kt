package com.flixxo.apps.flixxoapp.utils

import java.util.concurrent.TimeUnit

fun Long.timeFormat(): String {
    val stringTime = StringBuilder()
    val minutes = TimeUnit.SECONDS.toMinutes(this)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - (TimeUnit.SECONDS.toMinutes(this) * 60)

    if (minutes > 0) {
        stringTime.append("$minutes").append("m ")
    }
    if (seconds > 0) {
        stringTime.append("$seconds").append("s")
    }
    return stringTime.toString()

}