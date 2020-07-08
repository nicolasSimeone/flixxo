package com.flixxo.apps.flixxoapp.view

import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Content


sealed class CustomTypePopup {
    class BuyVideo(val content: Content?) : CustomTypePopup()
    object Credits : CustomTypePopup()
    object Contact : CustomTypePopup()

    fun getId(): Int = when (this) {
        is BuyVideo -> 1
        is Credits -> 2
        is Contact -> 3
    }

    fun getType(id: Int): CustomTypePopup = when (id) {
        1 -> BuyVideo(null)
        2 -> Credits
        3 -> Contact
        else -> Credits
    }

    fun getInfo(): CustomInfoPopup = when (this) {
        is BuyVideo -> {
            val price = this.content!!.price
            val message = "For this video you will be charged $price flixx"
            CustomInfoPopup(message = message, imageName = R.drawable.popup_wallet)
        }
        Credits -> {
            val message = "Oops! You don't have enough credits to watch this content."
            CustomInfoPopup(
                continueText = "WATCH AD",
                cancelText = "BUY TOKENS",
                message = message,
                imageName = R.drawable.popup_smiley
            )
        }
        Contact -> {
            val message =
                "An administrator will contact you soon at hernanb@lagash.com to complete the operation outside Flixxo."
            CustomInfoPopup(cancelText = "", message = message, imageName = R.drawable.popup_lock)
        }
    }


}

