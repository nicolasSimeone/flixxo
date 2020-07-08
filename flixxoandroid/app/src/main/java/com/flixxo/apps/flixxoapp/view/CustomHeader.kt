package com.flixxo.apps.flixxoapp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.flixxo.apps.flixxoapp.R

class CustomHeader(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_header, this, true)
        val view = LayoutInflater.from(context).inflate(R.layout.custom_header, this, true)
    }
}