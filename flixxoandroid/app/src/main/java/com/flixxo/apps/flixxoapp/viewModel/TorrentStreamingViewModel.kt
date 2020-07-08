package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.LoadMessages
import com.flixxo.apps.flixxoapp.repositories.TorrentRepository
import com.flixxo.apps.flixxoapp.repositories.UserRepository

class TorrentStreamingViewModel(
    private val torrentRepository: TorrentRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _messages = MutableLiveData<LoadMessages>()
    val messages: LiveData<LoadMessages>
        get() = _messages


    fun getLoadingMessages() {
        launchDataLoad {

            val userLang = userRepository.getUserStatus().profile.lang
            val messages = torrentRepository.getLoadingMessages(userLang.toString())

            _messages.value = messages

        }
    }

}
