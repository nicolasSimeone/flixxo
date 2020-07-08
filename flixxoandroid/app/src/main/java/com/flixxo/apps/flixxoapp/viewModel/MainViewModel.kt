package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.BalanceResponse
import com.flixxo.apps.flixxoapp.repositories.AdvertisementRepository
import com.flixxo.apps.flixxoapp.repositories.MainRepository
import com.flixxo.apps.flixxoapp.utils.formatValue

class MainViewModel(
    private val mainRepository: MainRepository,
    private val advertisementRepository: AdvertisementRepository
) : BaseViewModel() {

    private val _balance = MutableLiveData<BalanceResponse>()
    val balance: LiveData<BalanceResponse>
        get() = _balance

    private val _adReward = MutableLiveData<String>()

    fun loadBalance() {
        launchDataLoad {
            _balance.value = mainRepository.getBalance()
        }
    }

    fun loadAdReward() {
        launchDataLoad {
            _adReward.value = advertisementRepository.getAdvertisement().formatValue()
        }
    }

    fun updateClientKey(clientKey: String) {
        launchDataLoad {
            mainRepository.updateClientKey(clientKey)
        }
    }

    fun registerClientSeed(seed: String) = mainRepository.setClientSeed(seed)
}