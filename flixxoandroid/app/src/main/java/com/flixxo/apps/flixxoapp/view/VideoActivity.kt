package com.flixxo.apps.flixxoapp.view

import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.flixxo.apps.flixxoapp.R
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    var str_url: String = "https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        playVideo()

        close_video.setOnClickListener {
            finish()
        }
    }

    private fun playVideo() {
        videoview.setVideoPath(str_url)
        videoview.start()
        mediaController = MediaController(this)
        mediaController.setAnchorView(videoview)
        videoview.setMediaController(mediaController)

        videoview.setOnPreparedListener {
            progressbar.visibility = View.GONE
            close_video.visibility = View.VISIBLE
        }
    }

}
