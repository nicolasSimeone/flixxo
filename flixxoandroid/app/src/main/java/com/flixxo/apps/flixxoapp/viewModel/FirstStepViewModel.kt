package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.Country
import com.flixxo.apps.flixxoapp.model.Language
import com.flixxo.apps.flixxoapp.model.Profile
import com.flixxo.apps.flixxoapp.model.User
import com.flixxo.apps.flixxoapp.repositories.CountryRepository
import com.flixxo.apps.flixxoapp.repositories.UserRepository

class FirstStepViewModel(private val userRepository: UserRepository, private val countryRepository: CountryRepository) :
    BaseViewModel() {

    private val _country = MutableLiveData<List<Country>>()
    val country: LiveData<List<Country>>
        get() = _country

    private val _userUpdate = MutableLiveData<User>()

    private val _language = MutableLiveData<List<Language>>()
    val language: LiveData<List<Language>>
        get() = _language

    fun getLanguageCode(langEdit: String): String {
        val lang = language.value!!.single { language -> language.nameNative == langEdit }
        return lang.lang!!
    }

    fun getLanguages() {
        launchDataLoad {
            _language.value = userRepository.getLanguages()
        }
    }

    fun loadCountries() {
        launchDataLoad {
            _country.value = countryRepository.getCountries()
        }
    }

    fun updateProfile(name: String?, lang: String, country: String?, gender: String, birthdate: String) {
        launchDataLoad {
            val userProfile = Profile()
            userProfile.realName = name
            userProfile.lang = lang
            userProfile.birthDate = birthdate
            userProfile.gender = gender

            val countryId = _country.value!!.first { it.name.value == country }.id
            userProfile.countryId = countryId

            _userUpdate.value = userRepository.userProfile(userProfile)

        }

    }
}