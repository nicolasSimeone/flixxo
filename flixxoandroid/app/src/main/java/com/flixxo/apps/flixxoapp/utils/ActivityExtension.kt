package com.flixxo.apps.flixxoapp.utils

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flixxo.apps.flixxoapp.R

fun AppCompatActivity.showAlert(
    title: String,
    message: String,
    positiveButton: Pair<CharSequence, DialogInterface.OnClickListener?>,
    negativeButton: Pair<CharSequence, DialogInterface.OnClickListener?>?
) {
    val alert = AlertDialog.Builder(this, R.style.CustomDialog)
    alert.setTitle(title)
    alert.setMessage(message)
    alert.setPositiveButton(positiveButton.first, positiveButton.second)
    negativeButton?.let { alert.setNegativeButton(it.first, it.second) }
    alert.setCancelable(false)
    alert.show()
}

fun AppCompatActivity.hideControlBar(hasFocus: Boolean) {
    if (hasFocus) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}

fun AppCompatActivity.getStringById(id: String): Int {
    return resources.getIdentifier(id, "string", packageName)
}

fun Fragment.getStringById(id: String): Int {
    return resources.getIdentifier(id, "string", context!!.packageName)
}