package com.flixxo.apps.flixxoapp.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.formatDate(day: Int, month: Int, year: Int): String {
    val completeDay = if (day < 10) {
        "0$day"
    } else {
        "$day"
    }
    val completeMonth = if (month + 1 < 10) {
        "0${month + 1}"
    } else {
        "${month + 1}"
    }
    return """$completeDay/$completeMonth/$year"""
}

fun String.divideDate(birthdate: String?): Triple<Int, Int, Int> {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthdate)
    val calendar = Calendar.getInstance()
    calendar.time = date
    val yearSelected = calendar.get(Calendar.YEAR)
    val monthSelected = calendar.get(Calendar.MONTH) + 1
    val daySelected = calendar.get(Calendar.DAY_OF_MONTH)
    return Triple(yearSelected, monthSelected, daySelected)
}