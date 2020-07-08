package com.flixxo.apps.flixxoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

open class BaseViewModel : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    val error = MutableLiveData<String>()

    private val viewModelJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun launchDataLoad(block: suspend () -> Unit): Job {
        return scope.launch {
            try {
                _loading.value = true
                block()
            } catch (exception: Exception) {
                error.value = exception.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelAllPendingJobs() = scope.coroutineContext.cancelChildren()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}