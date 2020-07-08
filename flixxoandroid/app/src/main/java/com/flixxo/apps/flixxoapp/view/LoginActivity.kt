package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.LocaleHelper
import com.flixxo.apps.flixxoapp.utils.getStringById
import com.flixxo.apps.flixxoapp.viewModel.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class LoginActivity : AppCompatActivity() {

    lateinit var customProgressView: CustomProgressView
    private var callbackManager: CallbackManager? = null
    private val EMAIL = "email"
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle
    private val preferencesManager: PreferencesManager by inject()

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val language = Locale.getDefault().language
        LocaleHelper.setLocale(this, language)
        setContentView(R.layout.activity_login)

        customProgressView = CustomProgressView(this)

        loginButton.setOnClickListener {
            onTokenRefreshed()

            val userValid = username.nonEmpty {
                username.error = getString(R.string.invalidUsername)
            }

            val passwordValid = password.nonEmpty {
                password.error = getString(R.string.invalidPassword)
            }

            if (!userValid || !passwordValid) {
                return@setOnClickListener
            }

            viewModel.login(username.text.toString().toLowerCase(), password.text.toString())
        }

        callbackManager = CallbackManager.Factory.create()

        loginFacebook.setReadPermissions(Arrays.asList(EMAIL))

        loginFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // Retrieving access token using the LoginResult
                firebaseAnalytics.logEvent("login_facebook", bundle)

                viewModel.loginFacebook(loginResult.accessToken.token)

            }

            override fun onCancel() {}
            override fun onError(error: FacebookException) {}
        })


        viewModel.categories.observe(this, Observer { value ->
            LocaleHelper.onAttach(this, viewModel.user.value!!.profile.lang!!)

            value?.let { categoriesList ->
                when (viewModel.user.value!!.status) {
                    1 -> intent = Intent(this, CodeActivity::class.java)
                    in 2..3 -> intent = if (categoriesList.count() == 0) Intent(
                        this,
                        OnBoardStepsActivity::class.java
                    ) else Intent(this, MainActivity::class.java)
                    in 4..127 -> intent = Intent(this, MainActivity::class.java)
                    128 -> Toast.makeText(this, getString(R.string.userBanned), Toast.LENGTH_SHORT).show()
                }
                startActivity(intent)
                firebaseAnalytics.logEvent("login", bundle)
                finish()
            }
        })

        viewModel.error.observe(this, Observer { value ->
            value?.let { message ->
                Toast.makeText(this, getStringById(message), Toast.LENGTH_SHORT).show()
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

        if (viewModel.isUserLogged()) {
            val preferencesLanguage = preferencesManager.getString("USER_LANG")
            LocaleHelper.setLocale(this, preferencesLanguage)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun onRegisterClick(view: View) {
        val loginIntent = Intent(this, RegisterActivity::class.java)
        startActivity(loginIntent)

    }

    fun onClickToForgotPassword(view: View) {
        val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    public override fun onActivityResult(requestCode: Int, resulrCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resulrCode, data)
        super.onActivityResult(requestCode, resulrCode, data)
    }

    fun onTokenRefreshed() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.e("Failed")
                    return@OnCompleteListener
                }

                val token = task.result?.token

                val msg = "Message token $token"
                Timber.e(msg)
            })
    }
}