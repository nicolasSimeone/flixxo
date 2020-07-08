package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.CountryCodes
import com.flixxo.apps.flixxoapp.repositories.RegisterRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegisterViewModel(private val registerRepository: RegisterRepository) : BaseViewModel() {
    var user: MutableLiveData<String> = MutableLiveData()

    fun register(
        nickname: String,
        emailRegister: String,
        passwordRegister: String,
        code: String,
        mobile: Boolean,
        mobileNumber: String
    ) {
        launchDataLoad {
            val success =
                registerRepository.postRegister(nickname, emailRegister, passwordRegister, code, mobile, mobileNumber)

            if (success) {
                user.value = "User"
            }
        }
    }

    fun getCountryCodes(data: String): List<CountryCodes> {
        val type = object : TypeToken<List<CountryCodes>>() {}.type
        return Gson().fromJson<List<CountryCodes>>(data, type)
    }

}
