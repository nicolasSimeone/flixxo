package com.flixxo.apps.flixxoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.getStringById
import com.flixxo.apps.flixxoapp.viewModel.AccountViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.activity_account.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : Fragment() {

    lateinit var customProgressView: CustomProgressView

    private val viewModel: AccountViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProgressView = CustomProgressView(context!!)

        button_submit.setOnClickListener {

            val currentPass = current_pass.validator()
                .nonEmpty()
                .addErrorCallback {
                    showError(getString(R.string.validationCurrentPassEmpty))
                }
                .check()

            val passValidator = new_pass.validator()
                .nonEmpty()
                .atleastOneNumber()
                .atleastOneSpecialCharacters()
                .atleastOneUpperCase()
                .addErrorCallback {
                    showError(getString(R.string.invalid_pass))
                }
                .check()

            val passMatching = new_pass.validator()
                .textEqualTo(confirm_pass.text.toString())
                .addErrorCallback {
                    showError(getString(R.string.match_passwords))
                }
                .check()

            if (!passMatching || !currentPass || !passValidator) {
                return@setOnClickListener
            }

            viewModel.changePassword(current_pass.text.toString(), new_pass.text.toString())
        }

        viewModel.password.observe(this, Observer { value ->
            value?.let { message ->
                Toast.makeText(activity, getString(R.string.changePasswordSuccessfully), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.error.observe(this, Observer { value ->
            value?.let { message ->
                Toast.makeText(activity, getStringById(message), Toast.LENGTH_SHORT).show()
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


        email_user.text = "${viewModel.getUserEmail()}"
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    }
}