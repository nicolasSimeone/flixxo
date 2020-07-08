package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.Country
import com.flixxo.apps.flixxoapp.model.Language
import com.flixxo.apps.flixxoapp.model.Profile
import com.flixxo.apps.flixxoapp.model.User
import com.flixxo.apps.flixxoapp.repositories.CountryRepository
import com.flixxo.apps.flixxoapp.repositories.UserRepository
import com.flixxo.apps.flixxoapp.utils.formatDate
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val countryRepository: CountryRepository
) : BaseViewModel() {

    private lateinit var birthdate: String
    private lateinit var countries: List<Country>
    private var completeDate = ""

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _country = MutableLiveData<List<Country>>()
    val country: LiveData<List<Country>>
        get() = _country


    private val _userUpdate = MutableLiveData<User>()
    val userUpdate: LiveData<User>
        get() = _userUpdate

    private val _language = MutableLiveData<List<Language>>()
    val language: LiveData<List<Language>>
        get() = _language

    private val _avatar = MutableLiveData<Profile>()
    val avatar: LiveData<Profile>
        get() = _avatar

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

            val countryId = _country.value!!.first { it -> it.name.value == country }.id
            userProfile.countryId = countryId

            _userUpdate.value = userRepository.userProfile(userProfile)

        }
    }

    fun loadProfile() {
        launchDataLoad {
            val user = userRepository.getUserStatus()
            countries = countryRepository.getCountries()
            birthdate = user.profile.birthDate!!
            _user.value = user

        }
    }

    fun getCountryNamePosition(): Int {
        val index = countries.map { it.id }.indexOf(_user.value!!.profile.countryId)
        return if (index < 0) 0 else index
    }

    fun getLanguageCode(langEdit: String): String {
        val lang = language.value!!.single { language -> language.nameNative == langEdit }
        return lang.lang!!
    }


    fun getLanguagePosition(langCode: String): Int {
        val index = language.value!!.map { it.lang }.indexOf(langCode)
        return if (index < 0) 0 else index
    }


    fun getFormatBirthdate(): String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthdate)
        val cal = Calendar.getInstance()
        cal.time = date
        val month = cal.get(Calendar.MONTH)
        val dd = cal.get(Calendar.DATE)
        val year = cal.get(Calendar.YEAR)
        return completeDate.formatDate(dd, month + 1, year)
    }

    fun getLanguages() {
        launchDataLoad {
            _language.value = userRepository.getLanguages()
        }
    }

    fun deletePhoto() {
        launchDataLoad {
            userRepository.deletePhoto()
        }
    }


    fun changePhoto(avatar: String) {
        launchDataLoad {
            _avatar.value = userRepository.changePhoto(File(avatar))
        }
    }

}