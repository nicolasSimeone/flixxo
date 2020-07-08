package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.getStringById
import com.flixxo.apps.flixxoapp.viewModel.ForgotPasswordViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import kotlinx.android.synthetic.main.forgot_pass.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordActivity : AppCompatActivity() {
    private val viewModel: ForgotPasswordViewModel by viewModel()
    private lateinit var customProgressView: CustomProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_pass)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        customProgressView = CustomProgressView(this)

        submit_button.setOnClickListener {

            val emailForgot = email_forgot.validEmail {
                email_forgot.error = getString(R.string.invalidEmail)
            }

            if (!emailForgot) {
                return@setOnClickListener
            }

            viewModel.forgotPassword(email_forgot.text.toString())
        }

        viewModel.success.observe(this, Observer { success ->
            success?.let {
                Toast.makeText(this, getStringById("check_your_email"), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("EMAIL", email_forgot.text.toString())
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let { message ->
                Toast.makeText(this, getStringById(message), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    customProgressView.showLoadingDialog()
                } else {
                    customProgressView.hideLoadingDialog()
                }
            }
        })
    }

    fun getStartedClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onClickBack(view: View) {
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

}