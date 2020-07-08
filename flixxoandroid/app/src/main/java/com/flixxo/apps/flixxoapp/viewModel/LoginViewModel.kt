package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.model.User
import com.flixxo.apps.flixxoapp.repositories.LoginFacebookRepository
import com.flixxo.apps.flixxoapp.repositories.LoginRepository
import com.flixxo.apps.flixxoapp.repositories.UserRepository


class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
    private val loginFacebookRepository: LoginFacebookRepository
) : BaseViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories

    fun login(username: String,password: String) {
        launchDataLoad {
            val success = loginRepository.postLogin(username, password)

            if (success) {
                _user.value = userRepository.getUserStatus()
                _categories.value = loginRepository.getFollowedCategories()
            }
        }
    }

    fun loginFacebook(code: String) {
        launchDataLoad {
            val success = loginFacebookRepository.postLoginFacebook(code)

            if (success) {
                _user.value = userRepository.getUserStatus()
                _categories.value = loginRepository.getFollowedCategories()
            }
        }
    }

    fun isUserLogged() = loginRepository.isUserLogged()
}