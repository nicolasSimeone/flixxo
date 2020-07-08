package com.flixxo.apps.flixxoapp.view

import androidx.fragment.app.Fragment

open class OBStepFragment : Fragment() {

    var callback: OnBoardingCallback? = null

    fun continueTo() {
        callback?.continueToNextStep()
    }
}