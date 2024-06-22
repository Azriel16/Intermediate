package com.dicoding.intermediate.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.intermediate.data.pref.UserModel
import com.dicoding.intermediate.data.pref.UserPreference
import com.dicoding.intermediate.data.remote.paging.StoryPagingSource
import com.dicoding.intermediate.data.remote.response.AddStoryResponse
import com.dicoding.intermediate.data.remote.response.ListStoryItem
import com.dicoding.intermediate.data.remote.response.LoginResponse
import com.dicoding.intermediate.data.remote.response.RegisterResponse
import com.dicoding.intermediate.data.remote.response.StoryResponse
import com.dicoding.intermediate.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    private val _newStory = MutableLiveData<ListStoryItem>()
    val newStory: LiveData<ListStoryItem> = _newStory

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun getLogin(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getStories() = Pager(
        config = PagingConfig(pageSize = 5),
        pagingSourceFactory = {
            runBlocking {
                StoryPagingSource(apiService, userPreference.getSession().first().token)
            }
        }
    ).liveData

    suspend fun uploadImage(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?): AddStoryResponse {
        val user = userPreference.getSession().first()
        return apiService.uploadImage("Bearer ${user.token}", photo, description, lat, lon)
    }
    fun getStoriesWithLocation(): LiveData<List<ListStoryItem>> {
        val storiesWithLocation = MutableLiveData<List<ListStoryItem>>()
        return storiesWithLocation
    }

    fun getPagingStories(): Flow<PagingData<ListStoryItem>> {
        val token = runBlocking { userPreference.getSession().first().token }
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).flow
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
