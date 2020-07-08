package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.repositories.UserRepository

class DepositViewModel(private val userRepository: UserRepository) : BaseViewModel() {
    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    fun getToken() {
        launchDataLoad {
            _token.value = userRepository.getToken()
        }
    }

    fun createToken() {
        launchDataLoad {
            _token.value = userRepository.createToken()
        }
    }
}