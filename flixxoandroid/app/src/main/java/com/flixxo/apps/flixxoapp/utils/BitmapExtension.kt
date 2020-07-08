package com.flixxo.apps.flixxoapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

fun Bitmap.getImagePath(context: Context): String {
    val bytes = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, this, "Title", null)
    return Uri.parse(path).getFullPath(context)

}