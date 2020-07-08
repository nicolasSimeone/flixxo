package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import kotlinx.android.synthetic.main.no_token_ad.*
import org.koin.android.ext.android.inject

class NoFlixxAdActivity : AppCompatActivity() {

    private lateinit var playButton: ImageView
    private lateinit var cancelButton: Button
    private var price: Double = 0.0

    private val preferencesManager: PreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_token_ad)

        playButton = findViewById(R.id.play_video_ad)
        cancelButton = findViewById(R.id.cancel_ad)

        price = preferencesManager.getString("FLIXX_REWARD")!!.toDouble()
        var priceFormatted = String.format("%.2f", price)


        price_add.text = "$priceFormatted flixx"

        play_video_ad.setOnClickListener {
            playAd()
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun playAd() {

        SingletonAdPlayerHandler.instance.finishAction = { finish() }
        startActivity(Intent(applicationContext, AdPlayerActivity::class.java))
    }
}