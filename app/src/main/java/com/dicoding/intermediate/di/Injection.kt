package com.dicoding.intermediate.di

import android.content.Context
import com.dicoding.intermediate.data.UserRepository
import com.dicoding.intermediate.data.pref.UserPreference
import com.dicoding.intermediate.data.pref.dataStore
import com.dicoding.intermediate.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(userPreference, apiService)
    }
}
