package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.LocaleHelper
import com.flixxo.apps.flixxoapp.utils.formatValue
import org.koin.android.ext.android.inject

class OnBoardFourthStepFragment : OBStepFragment() {

    companion object {
        fun newInstance() = OnBoardFourthStepFragment()
    }

    private lateinit var title: TextView
    private lateinit var label: TextView
    private lateinit var continueButton: Button
    private lateinit var imageView: ImageView
    private lateinit var textRock1: TextView
    private lateinit var textRock2: TextView

    private val preferencesManager: PreferencesManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.onbarding_step4, container, false)
        LocaleHelper.onAttach(this.context!!)

        title = view.findViewById(R.id.title)
        continueButton = view.findViewById(R.id.continue_button_step4)
        imageView = view.findViewById(R.id.ad_was_view)
        textRock1 = view.findViewById(R.id.fixx_text_rock1)

        continueButton.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        setLabelAttributedText()
        return view
    }

    private fun setLabelAttributedText() {
        val price = preferencesManager.getString("FLIXX_REWARD")

        if (price == null) {
            textRock1.text = getString(R.string.skipNoTokens)
            title.text = getString(R.string.soSad)
            imageView.setBackgroundResource(R.drawable.ic_close_circle)
            return
        }
        val priceFormat = price.toDouble().formatValue()
        title.text = getString(R.string.rock_on)
        textRock1.text = String.format(getString(R.string.youEarned), priceFormat)
        imageView.setBackgroundResource(R.drawable.ic_tick_circle)
    }
}