package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.*
import com.flixxo.apps.flixxoapp.repositories.remote.service.UserService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UserRepository(private val remoteDataSource: UserService) {

    suspend fun forgotPassword(email: String): Boolean = withContext(Dispatchers.IO) {
        val result = remoteDataSource.forgotPassword(email)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun resetPassword(email: String, code: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val result = remoteDataSource.resetPassword(email, code, password)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun changePassword(currentPass: String, newPass: String) = withContext(Dispatchers.IO) {
        val result = remoteDataSource.changePassword(currentPass, newPass)
        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun userProfile(profile: Profile): User = withContext(Dispatchers.IO) {
        val result = remoteDataSource.userProfile(profile)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getUserStatus(): User = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getUserStatus()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getFollowers(): List<Author> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getFollowers()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getFollowings(): List<Author> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getFollowings()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getToken(): String = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getToken()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun createToken(): String = withContext(Dispatchers.IO) {
        val result = remoteDataSource.createToken()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getUserKeys(): List<ClientKey> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getClientKeys()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getLanguages(): List<Language> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getLanguagesFromApi()
        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun changePhoto(image: File): Profile = withContext(Dispatchers.IO) {
        val result = remoteDataSource.changePhoto(image)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    fun userHasKey() = remoteDataSource.hasClientKey()

    suspend fun deletePhoto(): Boolean = withContext(Dispatchers.IO) {
        val result = remoteDataSource.deletePhoto()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun followById(id: String): List<Author> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.followById(id)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun unfollowById(id: String): List<Author> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.unfollowById(id)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
