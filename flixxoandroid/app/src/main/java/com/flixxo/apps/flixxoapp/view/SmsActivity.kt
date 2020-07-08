package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.getStringById
import com.flixxo.apps.flixxoapp.utils.readAssetFile
import com.flixxo.apps.flixxoapp.viewModel.RegisterViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.activity_sms.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SmsActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModel()
    lateinit var customProgressView: CustomProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)


        val nickname = intent.extras?.get("Nickname_Register") as String
        val email = intent.extras?.get("Email_Register") as String
        val password = intent.extras?.get("Password_Register") as String
        val mobileNumber = StringBuilder()
        val mobile = true
        val codeArea = findViewById<TextView>(R.id.code_area)

        customProgressView = CustomProgressView(this)

        val data = assets.readAssetFile("country_phone_codes.json")
        val codes = viewModel.getCountryCodes(data)

        val countriesList = ArrayList<String>()
        codes.forEach {
            val countryAndCode = StringBuilder()
            countryAndCode.append(it.name.toString()).append("    ").append(it.dial_code.toString())
            countriesList.add(countryAndCode.toString())
        }

        val spinnerCountries = findViewById<Spinner>(R.id.spinner_countries)
        val adapter = ArrayAdapter(this, R.layout.item_codes, R.id.countries, countriesList)
        spinnerCountries.adapter = adapter

        spinnerCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val codeText = spinnerCountries.selectedItem.toString()
                codeArea.text = getString(R.string.code_area, codeText.substringAfterLast("+"))
            }
        }

        submit_sms.setOnClickListener {
            val phoneNumber = phone_number.text.toString()
            val phoneValidation = phoneNumber.validator()
                .nonEmpty()
                .minLength(4)
                .validNumber()
                .addErrorCallback {
                    Toast.makeText(this, getString(R.string.invalid_phone), Toast.LENGTH_SHORT).show()
                }
                .check()

            if (!phoneValidation) {
                return@setOnClickListener
            } else {
                mobileNumber.append(codeArea.text.toString())
                mobileNumber.append(phoneNumber)
            }

            viewModel.register(nickname, email, password, "", mobile, mobileNumber.toString())
        }

        back_sms.setOnClickListener {
            finish()
        }

        viewModel.user.observe(this, Observer { success ->
            success?.let {
                val intent = Intent(this, CodeActivity::class.java)
                startActivity(intent)
            }
        })

        viewModel.error.observe(this, Observer { value ->
            value?.let { message ->
                Toast.makeText(this, getStringById(message), Toast.LENGTH_LONG).show()
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
    }

    fun takeToLoginClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}
