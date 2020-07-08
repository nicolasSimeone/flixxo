package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.viewModel.PopupViewModel
import kotlinx.android.synthetic.main.activity_popup_video.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PopupVideoActivity : AppCompatActivity() {

    private lateinit var info: CustomInfoPopup
    private lateinit var popupType: CustomTypePopup

    private val viewModel: PopupViewModel by viewModel()
    private var torrentData: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_video)

        intent.getIntExtra("type", 0)
        torrentData = intent.getByteArrayExtra("torrentData")

        continue_button.setOnClickListener {
            info.firstAction()
        }

        cancel_button.setOnClickListener {
            info.secondAction()
        }

        close_popup.setOnClickListener {
            finish()
        }

        loadView(popupType)
    }

    private fun loadView(type: CustomTypePopup) {
        this.popupType = type
        info = type.getInfo()
        decideActions()
        setButtonStyles()
    }

    private fun setButtonStyles() {

        setButtonShape()

        imageName.setImageDrawable(getDrawable(info.getImage()))
        message.text = info.getMessage()
        continue_button.text = info.getContinueText()
        cancel_button.text = info.getCancelText()

        if (info.getContinueText().isEmpty()) {
            continue_button.visibility = View.GONE
        }

        if (info.getCancelText().isEmpty()) {
            cancel_button.visibility = View.GONE
        }

    }

    private fun setButtonShape() {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(resources.getColor(R.color.macaroniAndCheese))
        shape.setStroke(3, resources.getColor(R.color.macaroniAndCheese))
        shape.cornerRadius = (50).toFloat()

        val shapeCancel = GradientDrawable()
        shapeCancel.shape = GradientDrawable.RECTANGLE
        shapeCancel.setStroke(3, Color.WHITE)
        shapeCancel.setColor(Color.TRANSPARENT)
        shapeCancel.cornerRadius = (50).toFloat()

        continue_button.background = shape
        cancel_button.background = shapeCancel
    }

    private fun decideActions() {
        when (popupType) {
            is CustomTypePopup.BuyVideo -> {
                // Play video
                info.firstAction = {

                    val content = (popupType as CustomTypePopup.BuyVideo).content!!

                    viewModel.success.observe(this, Observer {
                        if (!it) {
                            print(getString(R.string.paymentFailed))
                        }
                    })
                    val intent = Intent(this, TorrentStreamingActivity::class.java)
                    intent.putExtra(TorrentStreamingActivity.Companion.TORRENT_DATA, torrentData)
                    startActivity(intent)
                    finish()
                }
                info.secondAction = {
                    finish()
                }
            }

            is CustomTypePopup.Credits -> {
                // Watch ad
                info.firstAction = {

                }

                info.secondAction = {
                    loadView(CustomTypePopup.Contact)
                    viewModel.sendEmail()
                }
            }

            is CustomTypePopup.Contact -> {
                info.firstAction = {
                    finish()
                }
            }
        }
    }
}
