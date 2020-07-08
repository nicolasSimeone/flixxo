package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.*
import com.flixxo.apps.flixxoapp.repositories.ContentRepository
import com.flixxo.apps.flixxoapp.repositories.MainRepository
import com.flixxo.apps.flixxoapp.repositories.UserRepository
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.google.gson.Gson

class UserProfileViewModel(
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager,
    private val mainRepository: MainRepository,
    private val contentRepository: ContentRepository
) : BaseViewModel() {

    lateinit var contentDetail: Content

    private val _country = MutableLiveData<List<Country>>()
    val country: LiveData<List<Country>>
        get() = _country

    private val _followers = MutableLiveData<List<Author>>()
    val followers: LiveData<List<Author>>
        get() = _followers

    private val _followings = MutableLiveData<List<Author>>()
    val followings: LiveData<List<Author>>
        get() = _followings

    private val _followersUpdated = MutableLiveData<List<FollowState>>()
    val followersUpdated: LiveData<List<FollowState>>
        get() = _followersUpdated

    private val _followingsUpdated = MutableLiveData<List<FollowState>>()
    val followingsUpdated: LiveData<List<FollowState>>
        get() = _followingsUpdated

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _balance = MutableLiveData<BalanceResponse>()
    val balance: LiveData<BalanceResponse>
        get() = _balance

    private val _content = MutableLiveData<Content>()
    val content: LiveData<Content>
        get() = _content

    private val _contentList = MutableLiveData<MutableList<Content>>()
    val contentList: LiveData<MutableList<Content>>
        get() = _contentList

    fun getFollowers() {
        launchDataLoad {
            _followers.value = userRepository.getFollowers()
        }
    }

    fun getFollowings() {
        launchDataLoad {
            _followings.value = userRepository.getFollowings()
        }
    }


    fun getUsername(): String {
        val username = preferencesManager.getString("USER_NAME")
        return when (username == null) {
            true -> ""
            false -> username
        }
    }

    fun getProfile() {
        launchDataLoad {
            _user.value = userRepository.getUserStatus()
        }
    }

    fun getBalance() {
        launchDataLoad {
            _balance.value = mainRepository.getBalance()
        }
    }

    fun followingStateList(): ArrayList<FollowState> {

        if (_followings.value == null) {
            return ArrayList()
        }

        val userList = _followings.value!!.map { FollowState(it, true) }
        return userList as ArrayList<FollowState>
    }

    fun followersStateList(): ArrayList<FollowState> {

        if (_followers.value == null) {
            return ArrayList()
        }

        val followings = _followings.value ?: ArrayList()

        val userList = _followers.value!!.map { FollowState(it, followings.contains(it)) }
        return userList as ArrayList<FollowState>
    }

    fun followById(id: String) {
        launchDataLoad {
            _followers.value = userRepository.followById(id)
        }
    }

    fun unfollowById(id: String) {
        launchDataLoad {
            userRepository.unfollowById(id)
        }
    }

    fun search(word: String) {
        launchDataLoad {
            _contentList.value = contentRepository.getSearch(word, null).toMutableList()
        }
    }

    fun getVideos(authorId: String) {
        launchDataLoad {
            val gson = Gson()
            val filters = gson.toJson(listOf(AuthorFilter(authorId)))
            _contentList.value = contentRepository.getSearch(null, filters).toMutableList()
        }
    }

    fun followersUserUpdated() {
        launchDataLoad {
            val followers = userRepository.getFollowers()
            val followings = userRepository.getFollowings()

            _followersUpdated.value = followers.map { FollowState(it, followings.contains(it)) }
        }
    }

    fun followingsUserUpdated() {
        launchDataLoad {
            val followings = userRepository.getFollowings()
            _followingsUpdated.value = followings.map { FollowState(it, true) }
        }
    }
}