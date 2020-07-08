package com.masterwok.simplevlcplayer.common.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * Get color using [ContextCompat] and the provided [id].
 */
internal fun Context.getCompatColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)