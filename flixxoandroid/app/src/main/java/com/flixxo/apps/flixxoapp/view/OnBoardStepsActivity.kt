package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flixxo.apps.flixxoapp.R

class OnBoardStepsActivity : AppCompatActivity(), OnBoardingCallback {
    override fun continueToNextStep() {
        val nextStep: OnBoardingSteps? = currentStep.nextStep()

        nextStep?.let {
            paintIndicator(nextStep)
            val view = nextStep.getFragment()
            view.callback = this
            replaceFragment(view)
            currentStep = nextStep
            return
        }
        showHome()
    }

    private lateinit var firstView: View
    private lateinit var secondView: View
    private lateinit var thirdView: View
    private lateinit var fourthView: View

    private var currentStep = OnBoardingSteps.First

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_controller)

        firstView = findViewById(R.id.step1)
        secondView = findViewById(R.id.step2)
        thirdView = findViewById(R.id.step3)
        fourthView = findViewById(R.id.step4)

        val view = currentStep.getFragment()
        view.callback = this
        replaceFragment(view)

    }

    private fun replaceFragment(view: OBStepFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_steps_layout, view)
            .addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    private fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun paintIndicator(steps: OnBoardingSteps) {
        when (steps) {
            OnBoardingSteps.First -> {
                firstView.setBackgroundResource(R.drawable.onboarding_controller_view_pressed)
            }
            OnBoardingSteps.Second -> {
                secondView.setBackgroundResource(R.drawable.onboarding_controller_view_pressed)
            }
            OnBoardingSteps.Third -> {
                thirdView.setBackgroundResource(R.drawable.onboarding_controller_view_pressed)
            }
            OnBoardingSteps.Fourth -> {
                fourthView.setBackgroundResource(R.drawable.onboarding_controller_view_pressed)
            }
        }
    }

    private fun paintIndicatorPrevious(steps: OnBoardingSteps) {
        when (steps) {
            OnBoardingSteps.First -> {
                firstView.setBackgroundResource(R.drawable.onboarding_controler_view_unpressed)
            }
            OnBoardingSteps.Second -> {
                secondView.setBackgroundResource(R.drawable.onboarding_controler_view_unpressed)
            }
            OnBoardingSteps.Third -> {
                thirdView.setBackgroundResource(R.drawable.onboarding_controler_view_unpressed)
            }
            OnBoardingSteps.Fourth -> {
                fourthView.setBackgroundResource(R.drawable.onboarding_controler_view_unpressed)
            }
        }
    }

    override fun onBackPressed() {
        val previousStep: OnBoardingSteps? = currentStep.previousStep()

        if (currentStep != OnBoardingSteps.First && currentStep != OnBoardingSteps.Fourth) {
            paintIndicatorPrevious(currentStep)
            this.supportFragmentManager.popBackStack()
            currentStep = previousStep!!

        }

    }


}