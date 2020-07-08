package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flixxo.apps.flixxoapp.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    lateinit var customProgressView: CustomProgressView
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        customProgressView = CustomProgressView(this)

        buttonRegister.setOnClickListener {

            val nicknameValidator = nickname.validator()
                .nonEmpty()
                .minLength(3)
                .maxLength(16)
                .addErrorCallback {
                    showError(getString(R.string.invalid_nickname))
                }
                .check()

            val regexValidation = validateEmailRegex(emailRegister.text.toString())
            if(!regexValidation) showError(getString(R.string.invalid_email))

            val emailValidator = emailRegister.validator()
                .nonEmpty()
                .addErrorCallback {
                    showError(getString(R.string.invalid_email))
                }
                .check()

            val passwordValidator = passwordRegister.validator()
                .nonEmpty()
                .atleastOneNumber()
                .atleastOneSpecialCharacters()
                .atleastOneUpperCase()
                .addErrorCallback {
                    showError(getString(R.string.invalid_pass))
                }
                .check()

            val passAreMatching = passwordRegister.validator()
                .textEqualTo(confirmPass.text.toString())
                .addErrorCallback {
                    showError(getString(R.string.match_passwords))
                }
                .check()

            if (!nicknameValidator || !regexValidation || !emailValidator || !passwordValidator || !passAreMatching) {
                return@setOnClickListener
            }

            val intent = Intent(this, SmsActivity::class.java)
            intent.putExtra("Nickname_Register", nickname.text.toString())
            intent.putExtra("Email_Register", emailRegister.text.toString().toLowerCase())
            intent.putExtra("Password_Register", passwordRegister.text.toString())
            startActivity(intent)
            firebaseAnalytics.logEvent("register_success", bundle)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }

    fun takeToLoginClick(view: View) {
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun validateEmailRegex(email: String) : Boolean {
        val EMAIL_REGEX =  Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,6})$")
        return EMAIL_REGEX.matcher(email).matches()
    }

    fun redirectFlixxo(view: View) {
        val viewIntent = Intent(
            "android.intent.action.VIEW",
            Uri.parse("https://www.flixxo.com/terms.html")
        )
        startActivity(viewIntent)
    }


}