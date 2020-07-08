package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager

class OnBoardThreeStepFragment : OBStepFragment() {

    companion object {
        fun newInstance() = OnBoardThreeStepFragment()
    }

    private lateinit var playButton: ImageView
    private lateinit var continueStep: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.onboarding_step3, container, false)

        playButton = view.findViewById(R.id.play_video)
        continueStep = view.findViewById(R.id.continue_to_step4)

        playButton.setOnClickListener {
            playAd()
        }

        continueStep.setOnClickListener {
            val preferencesManager = PreferencesManager(context!!)
            preferencesManager.clearKey("FLIXX_REWARD")
            super.continueTo()
        }

        return view
    }

    private fun playAd() {
        SingletonAdPlayerHandler.instance.finishAction = { super.continueTo() }
        val intent = Intent(context, AdPlayerActivity::class.java)
        intent.putExtra(AdPlayerActivity.EXTRA_PLAYCOINS, false)
        startActivity(intent)
    }

}