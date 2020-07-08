package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.repositories.CategoriesRepository

class OBSecondStepViewModel(private val categoriesRepository: CategoriesRepository) : BaseViewModel() {

    private val _category = MutableLiveData<List<Category>>()
    val category: LiveData<List<Category>>
        get() = _category

    private val _followedCategories = MutableLiveData<List<Category>>()
    val followedCategories: LiveData<List<Category>>
        get() = _followedCategories

    fun loadCategories() {
        launchDataLoad {
            _category.value = categoriesRepository.getCategories()
        }
    }

    fun setFollowedCategories(ids: List<Int>) {
        launchDataLoad {
            _followedCategories.value = categoriesRepository.followedCategories(ids)
        }
    }
}