package com.flixxo.apps.flixxoapp.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.flixxo.apps.flixxoapp.R


class CustomProgressView(context: Context) {

    private val dialog = Dialog(context)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setCancelable(false)

        if (dialog.window != null) {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        dialog.setContentView(R.layout.content_view)
    }

    fun showLoadingDialog() {
        dialog.show()
    }

    fun hideLoadingDialog() {
        dialog.dismiss()
    }

}