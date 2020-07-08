package com.flixxo.apps.flixxoapp.viewModel

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.*
import com.flixxo.apps.flixxoapp.repositories.ContentRepository
import com.flixxo.apps.flixxoapp.repositories.MainRepository
import com.flixxo.apps.flixxoapp.repositories.PaymentsRepository
import com.flixxo.apps.flixxoapp.repositories.UserRepository
import com.flixxo.apps.flixxoapp.utils.readAssetFile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DetailViewModel(
    private val contentRepository: ContentRepository,
    private val mainRepository: MainRepository,
    private val mPaymentsRepository: PaymentsRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    enum class UserContentStatus {
        Purchased,
        DontEnoughMoney,
        CanPurchase;
    }

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    private val _followings = MutableLiveData<List<Author>>()
    val followings: LiveData<List<Author>>
        get() = _followings

    private val _torrentFile = MutableLiveData<NewTorrentFile>()
    val torrentFile: LiveData<NewTorrentFile>
        get() = _torrentFile

    fun pay(uuid: String, price: Double) {
        launchDataLoad {
            val userName = userRepository.getUserStatus().profile.realName
            contentRepository.insertContentResultsDb(userName.toString(), uuid)
            val payed = mPaymentsRepository.payContent(uuid, price)
            if (payed) {
                val torrentFile = contentRepository.getTorrentFile(uuid)
                _torrentFile.value = torrentFile
            }
            _success.value = payed
        }
    }


    private val _balance = MutableLiveData<BalanceResponse>()
    val balance: LiveData<BalanceResponse>
        get() = _balance


    private val _content = MutableLiveData<Content>()
    val content: LiveData<Content>
        get() = _content

    private val _series = MutableLiveData<Series>()
    val series: LiveData<Series>
        get() = _series

    private val _seasons = MutableLiveData<List<Season>>()
    val seasons: LiveData<List<Season>>
        get() = _seasons

    private val _status = MutableLiveData<UserContentStatus>()
    val status: LiveData<UserContentStatus>
        get() = _status

    private val _isPurchased = MutableLiveData<Boolean>()
    val isPurchased: LiveData<Boolean>
        get() = _isPurchased

    private val _contentPurchased = MutableLiveData<List<ContentPurchased>>()
    val contentPurchased: LiveData<List<ContentPurchased>>
        get() = _contentPurchased

    private val _languages = MutableLiveData<List<Language>>()
    val languages: LiveData<List<Language>>
        get() = _languages

    fun loadBalance() {
        launchDataLoad {
            _balance.value = mainRepository.getBalance()
        }
    }

    fun getCompleteLanguage(assetManager: AssetManager, langCode: String): String {
        return getLanguages(assetManager).single { lang -> lang.value == langCode }.label.toString()
    }

    fun getLanguages(assetManager: AssetManager): List<LanguageJson> {
        val data = assetManager.readAssetFile("lang.json")
        val type = object : TypeToken<List<LanguageJson>>() {}.type
        return Gson().fromJson<List<LanguageJson>>(data, type)
    }

    fun loadDetail(uuid: String?, contType: Int?) {
        if (contType == 1) {
            uuid?.let {
                launchDataLoad {
                    _content.value = contentRepository.getContentDetail(it)
                }
            }
        } else {
            uuid?.let {
                launchDataLoad {
                    val series = contentRepository.getSeriesDetail(it)
                    _seasons.value = series.season
                    _series.value = series
                }
            }
        }
    }

    fun canUserPlay(uuid: String, price: Double) {
        launchDataLoad {
            val userName = userRepository.getUserStatus().profile.realName
            val purchased = contentRepository.getContentResultsDb(userName.toString())

            if (purchased.any { it.uuid == uuid }) {
                _status.value = UserContentStatus.Purchased
                return@launchDataLoad
            }

            if(uuid.isNotEmpty()) {
                val balance = mainRepository.getBalance()
                _status.value = if (balance.amount > price) UserContentStatus.CanPurchase else UserContentStatus.DontEnoughMoney
            }
        }
    }

    fun getContentPurchased() {
        launchDataLoad {
            val userName = userRepository.getUserStatus().profile.realName
            val all = contentRepository.getContentResultsDb(userName.toString())
            _contentPurchased.value = all
        }
    }

    fun getTorrentFile(uuid: String) {
        launchDataLoad {
            _torrentFile.value = contentRepository.getTorrentFile(uuid)
        }
    }

    fun getSeed() = mainRepository.getClientSeed()

    fun getFollowings() {
        launchDataLoad {
            _followings.value = userRepository.getFollowings()
        }
    }

}
