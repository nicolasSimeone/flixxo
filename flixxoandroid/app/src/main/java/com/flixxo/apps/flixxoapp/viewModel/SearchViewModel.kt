package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.repositories.ContentRepository

class SearchViewModel(private val contentRepository: ContentRepository) : BaseViewModel() {

    private val _content = MutableLiveData<MutableList<Content>>()
    val content: LiveData<MutableList<Content>>
        get() = _content

    fun search(word: String) {
        launchDataLoad {
            _content.value = contentRepository.getSearch(word, null).toMutableList()
        }
    }

    fun clearList() {
        _content.value = mutableListOf()
    }
}