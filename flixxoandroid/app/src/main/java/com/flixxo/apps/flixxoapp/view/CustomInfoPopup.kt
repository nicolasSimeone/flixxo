package com.flixxo.apps.flixxoapp.view

typealias Action = () -> Unit

class CustomInfoPopup(
    continueText: String = "Continue",
    cancelText: String = "Cancel",
    message: String,
    imageName: Int
) {

    private var continueTextPop: String = continueText.toUpperCase()
    private var cancelTextPop: String = cancelText.toUpperCase()
    private var messagePop: String = message
    private var imageNamePop: Int = imageName

    lateinit var firstAction: Action
    lateinit var secondAction: Action

    fun getContinueText(): String {
        return continueTextPop
    }

    fun getCancelText(): String {
        return cancelTextPop
    }

    fun getMessage(): String {
        return messagePop
    }

    fun getImage(): Int {
        return imageNamePop
    }

}