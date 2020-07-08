package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.getStringById
import com.flixxo.apps.flixxoapp.viewModel.ResetPasswordViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.textEqualTo
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.reset_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ResetPasswordActivity : AppCompatActivity() {

    private val viewModel: ResetPasswordViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password)

        take_login_reset.setOnClickListener {
            val code = enter_code.nonEmpty {
                enter_code.error = getString(R.string.incorrectCode)
            }

            val passwordValidator = new_password.validator()
                .atleastOneNumber()
                .atleastOneSpecialCharacters()
                .atleastOneUpperCase()
                .addErrorCallback {
                    new_password.error = (getString(R.string.invalid_pass))
                }
                .check()

            val equalPassword = confirm_password.textEqualTo(new_password.text.toString()) {
                confirm_password.error = getString(R.string.passwordMismatch)
            }

            if (!code || !equalPassword || !passwordValidator) {
                return@setOnClickListener
            }

            val emailForgot = intent.getStringExtra("EMAIL")
            viewModel.resetPassword(
                email = emailForgot,
                code = enter_code.text.toString(),
                password = new_password.text.toString()
            )
        }


        viewModel.success.observe(this, Observer { success ->
            success.let {
                Toast.makeText(this, getStringById("changePasswordSuccessfully"), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let { message ->
                Toast.makeText(this, getStringById(message), Toast.LENGTH_LONG).show()
            }
        })
    }

    fun takeToLoginClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}