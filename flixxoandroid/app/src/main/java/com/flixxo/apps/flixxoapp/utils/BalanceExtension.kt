package com.flixxo.apps.flixxoapp.utils

import java.util.*

fun Double.formatValue(): String {
    return String.format(Locale.US, "%.2f", this)
}