package com.dicoding.intermediate.data

import com.dicoding.intermediate.data.pref.UserModel
import com.dicoding.intermediate.data.pref.UserPreference
import com.dicoding.intermediate.data.remote.response.AddStoryResponse
import com.dicoding.intermediate.data.remote.response.LoginResponse
import com.dicoding.intermediate.data.remote.response.RegisterResponse
import com.dicoding.intermediate.data.remote.response.StoryResponse
import com.dicoding.intermediate.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

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

    suspend fun getStories(): StoryResponse {
        val user = userPreference.getSession().first()
        return apiService.getStories("Bearer ${user.token}")
    }

    suspend fun uploadImage(photo: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?): AddStoryResponse {
        val user = userPreference.getSession().first()
        return apiService.uploadImage("Bearer ${user.token}", photo, description)
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
