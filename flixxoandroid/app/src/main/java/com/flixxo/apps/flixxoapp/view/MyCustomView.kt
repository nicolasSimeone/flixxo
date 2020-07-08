package com.flixxo.apps.flixxoapp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.flixxo.apps.flixxoapp.R

class MyCustomView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    lateinit var balance: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.my_custom_view, this, true)
        val view = LayoutInflater.from(context).inflate(R.layout.my_custom_view, this, true)

        balance = view.findViewById(R.id.balance_amount)
    }
}