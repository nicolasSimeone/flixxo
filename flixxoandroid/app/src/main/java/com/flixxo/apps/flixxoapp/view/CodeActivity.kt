package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.facebook.login.LoginManager
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.getStringById
import com.flixxo.apps.flixxoapp.viewModel.ConfirmCodeViewModel
import kotlinx.android.synthetic.main.activity_code.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CodeActivity : AppCompatActivity() {

    lateinit var customProgressView: CustomProgressView

    private val viewModel: ConfirmCodeViewModel by viewModel()
    private lateinit var tryAgainMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        customProgressView = CustomProgressView(this)

        check_message.text = getString(R.string.sent_message_to, viewModel.getPhoneNumber())

        submit_code.setOnClickListener {
            viewModel.confirmSMS(edit_code.text.toString())
        }

        viewModel.code.observe(this, Observer { success ->
            success?.let {
                val intent = Intent(this, OnBoardStepsActivity::class.java)
                startActivity(intent)
            }
        })

        viewModel.error.observe(this, Observer { value ->
            value?.let { message ->

                val time = message.toIntOrNull()

                if (time != null) {
                    tryAgainMessage = when(time) {
                        0 -> String.format(getString(R.string.try_again_0_minutes))
                        1 -> String.format(getString(R.string.tryAgainMinute), time)
                        else -> String.format(getString(R.string.tryAgainMinutes), time)
                    }
                    Toast.makeText(this, tryAgainMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getStringById(message), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.loading.observe(this, Observer { value ->
            value?.let { show ->
                if (show) {
                    customProgressView.showLoadingDialog()
                } else {
                    customProgressView.hideLoadingDialog()
                }
            }
        })

        viewModel.resend.observe(this, Observer {
            it?.let {
                Toast.makeText(this, getString(R.string.updated_code), Toast.LENGTH_SHORT).show()
            }
        })

        resend_code.setOnClickListener {
            viewModel.resendCodeSMS()
        }
    }

    fun takeToLoginClick(view: View) {
        PreferencesManager.getInstance(this).clearKey("USER_SECRET")
        LoginManager.getInstance().logOut()
        val intent = Intent(this, LoginActivity::class.java)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        PreferencesManager.getInstance(this).clearKey("USER_SECRET")
        LoginManager.getInstance().logOut()
    }
}