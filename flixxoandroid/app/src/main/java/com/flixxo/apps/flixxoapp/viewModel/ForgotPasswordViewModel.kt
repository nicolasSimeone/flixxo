package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.repositories.UserRepository

class ForgotPasswordViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    fun forgotPassword(email: String) {
        launchDataLoad {
            _success.value = userRepository.forgotPassword(email)
        }
    }


}