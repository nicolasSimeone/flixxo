package com.flixxo.apps.flixxoapp.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.flixxo.apps.flixxoapp.R

fun ImageView.loadFrom(url: String) {
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.color.flixxoAppColor)

    Glide.with(this)
        .applyDefaultRequestOptions(requestOptions)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(250))
        .into(this)
}

fun ImageView.loadFromCustom(url: String) {
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.color.flixxoAppColor)

    Glide.with(this)
        .applyDefaultRequestOptions(requestOptions)
        .load(url)
        .into(this)
}