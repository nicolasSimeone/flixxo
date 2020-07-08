package com.flixxo.apps.flixxoapp.viewModel

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.*
import com.flixxo.apps.flixxoapp.repositories.AdvertisementRepository
import com.flixxo.apps.flixxoapp.repositories.CategoriesRepository
import com.flixxo.apps.flixxoapp.repositories.ContentRepository
import com.flixxo.apps.flixxoapp.repositories.UserRepository
import com.flixxo.apps.flixxoapp.utils.formatValue
import com.flixxo.apps.flixxoapp.utils.readAssetFile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class HomeViewModel(
    private val contentRepository: ContentRepository,
    private val categoriesRepository: CategoriesRepository,
    private val advertisementRepository: AdvertisementRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    enum class HomeContentType {
        Series,
        Community,
        All;
    }

    private val _seriesTop = MutableLiveData<List<Series>>()
    val seriesTop: LiveData<List<Series>>
        get() = _seriesTop


    private val _adReward = MutableLiveData<String>()
    val adReward: LiveData<String>
        get() = _adReward


    private val _topContents = MutableLiveData<List<Content>>()
    val topContents: LiveData<List<Content>>
        get() = _topContents

    private val _categoryContents = MutableLiveData<List<ContentCategory>>()
    val categoryContents: LiveData<List<ContentCategory>>
        get() = _categoryContents

    private val _seriesCategoryContents = MutableLiveData<List<ContentCategory>>()
    private val _communityCategoryContents = MutableLiveData<List<ContentCategory>>()

    private val _seriesTopContents = MutableLiveData<List<Series>>()
    private val _communityTopContents = MutableLiveData<List<Content>>()


    fun loadContentByCategories(type: HomeContentType) {
        when (type) {
            HomeContentType.Community -> {
                _communityCategoryContents.value?.let {
                    _categoryContents.value = it
                    return
                }
            }

            HomeContentType.Series -> {
                _seriesCategoryContents.value?.let {
                    _categoryContents.value = it
                    return
                }
            }
        }

        _getContentCategory(type)
    }


    fun getTopContent(type: HomeContentType) {
        cancelAllPendingJobs()

        when (type) {
            HomeContentType.Community -> {
                _communityTopContents.value?.let {
                    _topContents.value = it
                } ?: run {
                    _getTopContents()
                }
            }

            HomeContentType.Series -> {
                _seriesTopContents.value?.let {
                    _seriesTop.value = it
                } ?: run {
                    getTopSeries()
                }
            }
        }
    }

    fun getAd() {
        launchDataLoad {
            _adReward.value = advertisementRepository.getAdvertisement().formatValue()
        }
    }

    private fun _getContentCategory(type: HomeContentType) {
        var list: MutableList<ContentCategory> = arrayListOf()

        loadFakeCategories()

        launchDataLoad {
            val categories = categoriesRepository.getCategories().sortedBy { it.name }

            val categoriesRequests = arrayListOf<Deferred<Any>>()

            for (category in categories) {

                categoriesRequests += GlobalScope.async(Dispatchers.Main) {

                    val contents = contentRepository.getContentsByCategoryId(category.id, type.name.toLowerCase())

                    if (!contents.isEmpty()) {
                        val categoryContent = ContentCategory(category, contents)
                        list.add(categoryContent)
                    }
                }

            }

            categoriesRequests.forEach { it.await(); };

            getAd()

            var orderedList = list.sortedBy { it.category.name }

            _categoryContents.value = orderedList

            when (type) {
                HomeContentType.Community -> _communityCategoryContents.value = orderedList
                HomeContentType.Series -> _seriesCategoryContents.value = orderedList
            }
        }
    }

    fun getTopSeries() {
        cancelAllPendingJobs()

        launchDataLoad {
            _seriesTop.value = contentRepository.getTopSeries()
            _seriesTopContents.value = _seriesTop.value
        }
    }

    private fun _getTopContents() = launchDataLoad {
        val contentTop = contentRepository.getTopContent(HomeContentType.Community.name.toLowerCase())
        _topContents.value = contentTop.take(5)
        _communityTopContents.value = _topContents.value
    }


    fun getLanguages(assetManager: AssetManager): List<LanguageJson> {
        val data = assetManager.readAssetFile("lang.json")
        val type = object : TypeToken<List<LanguageJson>>() {}.type
        return Gson().fromJson<List<LanguageJson>>(data, type)
    }

    private fun loadFakeCategories() {
        val sortedCategories = listOf(Category(), Category(), Category(), Category())
        val categoryContentArray: MutableList<ContentCategory> = arrayListOf()

        sortedCategories.map {
            categoryContentArray.add(
                ContentCategory(
                    category = Category(name = "Loading..."),
                    contents = listOf(Content(), Content(), Content(), Content(), Content())
                )
            )
        }

        _categoryContents.value = categoryContentArray
    }

    fun updateContentDb() {
        launchDataLoad {
            val userName = userRepository.getUserStatus().profile.realName
            contentRepository.getContentPurchased(userName.toString())
        }
    }
}
