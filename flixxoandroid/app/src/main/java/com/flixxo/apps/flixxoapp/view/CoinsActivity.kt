package com.flixxo.apps.flixxoapp.view

import android.animation.Animator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.hideControlBar
import kotlinx.android.synthetic.main.activity_coins.*


class CoinsActivity : AppCompatActivity(), Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAnimationEnd(animation: Animator?) {
        this@CoinsActivity.finish()
    }

    override fun onAnimationCancel(animation: Animator?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAnimationStart(animation: Animator?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coins)
        hideControlBar(true)

        coins_lottie.addAnimatorListener(this)
    }
}
