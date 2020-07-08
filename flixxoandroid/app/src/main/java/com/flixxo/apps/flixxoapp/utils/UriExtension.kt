package com.flixxo.apps.flixxoapp.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun Uri.getFullPath(context: Context): String {
    val path: String
    val cursor = context.contentResolver.query(this, null, null, null, null)
    path = if (cursor == null)
        this.path!!
    else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        cursor.getString(idx)

    }
    cursor?.close()
    return path

}