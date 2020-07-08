package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.repositories.ConfirmCodeRepository
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager

class ConfirmCodeViewModel(
    private val confirmCodeRepository: ConfirmCodeRepository,
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {
    private val _code = MutableLiveData<Boolean>()
    val code: LiveData<Boolean>
        get() = _code

    private val _resend = MutableLiveData<Boolean>()
    val resend: LiveData<Boolean>
        get() = _resend

    fun confirmSMS(code: String) {
        launchDataLoad {
            _code.value = confirmCodeRepository.confirmSMS(code)
        }
    }

    fun getPhoneNumber(): String? {
        val phoneNumber = preferencesManager.getString("PHONE_NUMBER")
        return when (phoneNumber.isNullOrEmpty()) {
            true -> ""
            false -> phoneNumber
        }
    }

    fun resendCodeSMS() {
        launchDataLoad {
            _resend.value = confirmCodeRepository.resendCodeSMS()

        }
    }
}