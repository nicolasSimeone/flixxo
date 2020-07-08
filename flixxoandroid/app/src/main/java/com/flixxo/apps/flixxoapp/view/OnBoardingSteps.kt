package com.flixxo.apps.flixxoapp.view

interface OnBoardingCallback {
    fun continueToNextStep()
}

enum class OnBoardingSteps {
    First,
    Second,
    Third,
    Fourth;

    fun getFragment(): OBStepFragment = when (this) {
        First -> {
            OnBoardFirstStepFragment.newInstance()
        }
        Second -> {
            OBSecondStepFragment.newInstance()
        }
        Third -> {
            OnBoardThreeStepFragment.newInstance()
        }
        Fourth -> {
            OnBoardFourthStepFragment.newInstance()
        }
    }

    fun nextStep(): OnBoardingSteps? {
        return when (this) {
            First -> Second
            Second -> Third
            Third -> Fourth
            Fourth -> null
        }
    }

    fun previousStep(): OnBoardingSteps? {
        return when (this) {
            First -> null
            Second -> First
            Third -> Second
            Fourth -> Third
        }
    }


    fun isLastOne(): Boolean {
        return nextStep() == null
    }

}

