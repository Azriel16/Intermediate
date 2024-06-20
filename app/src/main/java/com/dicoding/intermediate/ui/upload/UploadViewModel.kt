package com.dicoding.intermediate.ui.upload

import androidx.lifecycle.ViewModel
import com.dicoding.intermediate.data.UserRepository
import com.dicoding.intermediate.data.remote.response.AddStoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val userRepository: UserRepository) : ViewModel() {

    suspend fun uploadImage(file: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?): AddStoryResponse {
        return withContext(Dispatchers.IO) {
            userRepository.uploadImage(file, description, lat, lon)
        }
    }
}
