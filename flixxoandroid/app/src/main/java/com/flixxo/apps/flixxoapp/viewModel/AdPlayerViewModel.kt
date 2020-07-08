package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.AdWatchedResponse
import com.flixxo.apps.flixxoapp.model.AdvertisementResponse
import com.flixxo.apps.flixxoapp.repositories.AdvertisementRepository

class AdPlayerViewModel(private val advertisementRepository: AdvertisementRepository) : BaseViewModel() {

    private val _adPlayer = MutableLiveData<AdvertisementResponse>()
    val adPlayer: LiveData<AdvertisementResponse>
        get() = _adPlayer

    private val _adWatched = MutableLiveData<AdWatchedResponse>()

    private val _adReward = MutableLiveData<Double>()

    fun getAdVideo() {
        launchDataLoad {
            _adPlayer.value = advertisementRepository.getAdPlay()
        }
    }

    fun adWatched(id: Int) {
        launchDataLoad {
            _adWatched.value = advertisementRepository.adWatched(id)
        }
    }

    fun loadadReward() {
        launchDataLoad {
            _adReward.value = advertisementRepository.getAdvertisement()
        }
    }
}