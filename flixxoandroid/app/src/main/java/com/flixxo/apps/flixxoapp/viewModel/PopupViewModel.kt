package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.repositories.MailRepository
import com.flixxo.apps.flixxoapp.repositories.PaymentsRepository

class PopupViewModel(private val paymentsRepository: PaymentsRepository, private val mailRepository: MailRepository) :
    BaseViewModel() {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    fun sendEmail() {
        launchDataLoad {
            mailRepository.sendEmail()
        }
    }
}