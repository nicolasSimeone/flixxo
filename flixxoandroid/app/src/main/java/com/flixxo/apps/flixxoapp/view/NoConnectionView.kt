package com.flixxo.apps.flixxoapp.view

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.flixxo.apps.flixxoapp.R
import com.google.firebase.analytics.FirebaseAnalytics


class NoConnectionView(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {

    var tryAgain: Button
    private var firebaseAnalytics: FirebaseAnalytics
    private var bundle: Bundle

    lateinit var tryAgainAction: () -> Unit

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_no_connection, this, true)
        tryAgain = view.findViewById(R.id.try_again)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        bundle = Bundle()

        tryAgain.setOnClickListener {
            if (::tryAgainAction.isInitialized) {
                tryAgainAction()
            }
        }

    }
}
