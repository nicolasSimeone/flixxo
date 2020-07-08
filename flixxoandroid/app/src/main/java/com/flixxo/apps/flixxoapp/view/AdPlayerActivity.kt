package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.MediaController
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.AdvertisementResponse
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.hideControlBar
import com.flixxo.apps.flixxoapp.viewModel.AdPlayerViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_ad_player.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingletonAdPlayerHandler private constructor() {
    var finishAction: (() -> Unit)? = null

    fun execute() {
        finishAction?.invoke()
        finishAction = null
    }

    companion object {
        val instance by lazy { SingletonAdPlayerHandler() }
    }
}

class VideoListener : SeekBar.OnSeekBarChangeListener {
    lateinit var seekBar: SeekBar


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        seekBar?.isEnabled = false
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.isEnabled = false
    }
}

class AdPlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PLAYCOINS = "PLAYCOINS"
    }

    private val viewModel: AdPlayerViewModel by viewModel()

    private lateinit var advertisement: AdvertisementResponse
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle
    private var isReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ad_player)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.logEvent(getString(R.string.adPlayerScreen), bundle)


        val mediaController = MediaController(this, false)
        mediaController.setAnchorView(videoview)

        viewModel.adPlayer.observe(this, Observer {
            val url = it.url.toString()
            advertisement = it
            videoview.setVideoPath(url)
            videoview.start()
        })

        videoview.setOnCompletionListener {
            viewModel.adWatched(advertisement.id!!)
            SingletonAdPlayerHandler.instance.execute()
            val playCoins = intent.extras?.getBoolean(EXTRA_PLAYCOINS) ?: true
            finish()
            if (playCoins) {
                startActivity(Intent(this, CoinsActivity::class.java))
            }
            firebaseAnalytics.logEvent(getString(R.string.adFinished), bundle)
        }

        videoview.setOnPreparedListener {
            isReady = true
            progressbar.visibility = View.GONE
            close_ad.visibility = View.VISIBLE

            val seekBarId = resources.getIdentifier("mediacontroller_progress", "id", "android")
            val seekBar = mediaController.findViewById<SeekBar>(seekBarId)

            val listener = VideoListener()
            listener.seekBar = seekBar



            seekBar.setOnSeekBarChangeListener(listener)
        }

        videoview.setOnTouchListener { v, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN && isReady) {
                try {
                    videoview.setMediaController(mediaController)
                    mediaController.show(3000)
                    val listener = VideoListener()
                    val seekBarId = resources.getIdentifier("mediacontroller_progress", "id", "android")
                    val seekBar = mediaController.findViewById<SeekBar>(seekBarId)
                    listener.seekBar = seekBar
                    seekBar.setOnSeekBarChangeListener(listener)
                } catch (e: Exception) {

                }
            }
            true

        }
        close_ad.setOnClickListener {
            val preferencesManager = PreferencesManager(this)
            preferencesManager.clearKey("FLIXX_REWARD")
            SingletonAdPlayerHandler.instance.execute()
            finish()
            firebaseAnalytics.logEvent(getString(R.string.adCancelled), bundle)
        }
        videoview.setMediaController(null)
        viewModel.getAdVideo()
        viewModel.loadadReward()
        hideControlBar(true)
    }

    override fun onResume() {
        super.onResume()
        hideControlBar(true)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideControlBar(hasFocus)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val preferencesManager = PreferencesManager(this)
        preferencesManager.clearKey("FLIXX_REWARD")
        SingletonAdPlayerHandler.instance.execute()
        finish()
    }

}