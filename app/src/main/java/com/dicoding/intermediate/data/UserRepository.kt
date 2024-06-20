package com.dicoding.intermediate.data

import com.dicoding.intermediate.data.pref.UserModel
import com.dicoding.intermediate.data.pref.UserPreference
import com.dicoding.intermediate.data.remote.response.StoryResponse
import com.dicoding.intermediate.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

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
        // Get the user session to retrieve the token
        val user = userPreference.getSession().first()
        val token = user.token

        // Use the token to get stories
        return apiService.getStories("Bearer $token")
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
