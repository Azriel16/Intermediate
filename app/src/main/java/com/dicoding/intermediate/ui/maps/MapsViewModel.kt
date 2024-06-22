package com.dicoding.intermediate.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.intermediate.data.UserRepository
import com.dicoding.intermediate.data.remote.response.ListStoryItem

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getStoriesWithLocation(): LiveData<List<ListStoryItem>> {
        return repository.getStoriesWithLocation()
    }
}
