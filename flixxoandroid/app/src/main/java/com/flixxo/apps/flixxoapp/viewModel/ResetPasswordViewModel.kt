package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.repositories.UserRepository

class ResetPasswordViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    fun resetPassword(email: String, code: String, password: String) {
        launchDataLoad {
            _success.value = userRepository.resetPassword(email, code, password)
        }
    }

}