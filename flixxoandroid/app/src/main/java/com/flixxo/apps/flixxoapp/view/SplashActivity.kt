package com.flixxo.apps.flixxoapp.view

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.hideControlBar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity(), Animator.AnimatorListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        hideControlBar(hasFocus)
    }

    override fun onAnimationRepeat(animation: Animator?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAnimationEnd(animation: Animator?) {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        this@SplashActivity.finish()
    }

    override fun onAnimationCancel(animation: Animator?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAnimationStart(animation: Animator?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.logEvent("splash_screen", bundle)

        animationView.addAnimatorListener(this)
    }
}
