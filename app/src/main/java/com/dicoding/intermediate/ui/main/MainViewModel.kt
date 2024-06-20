package com.dicoding.intermediate.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate.data.UserRepository
import com.dicoding.intermediate.data.pref.UserModel
import com.dicoding.intermediate.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchStories() {
        viewModelScope.launch {
            try {
                val response = repository.getStories()
                _stories.value = response.story
            } catch (e: Exception) {
                _error.value = "Failed to fetch stories: ${e.message}"
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
