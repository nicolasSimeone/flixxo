package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.repositories.UserRepository
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager

class AccountViewModel(private val userRepository: UserRepository, private val preferencesManager: PreferencesManager) :
    BaseViewModel() {
    private val _password = MutableLiveData<Boolean>()
    val password: LiveData<Boolean>
        get() = _password

    fun changePassword(currentPass: String, newPass: String) {
        launchDataLoad {
            _password.value = userRepository.changePassword(currentPass, newPass)
        }
    }

    fun getUserEmail(): String? {
        val email = preferencesManager.getString("USER_EMAIL") ?: ""
        return when (email.isEmpty()) {
            true -> ""
            false -> email
        }
    }
}