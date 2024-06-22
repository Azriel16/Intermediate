package com.dicoding.intermediate.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.intermediate.data.UserRepository
import com.dicoding.intermediate.data.pref.UserModel
import com.dicoding.intermediate.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> = repository.getPagingStories().cachedIn(viewModelScope).asLiveData()
    val newStory: LiveData<ListStoryItem> = repository.newStory

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
