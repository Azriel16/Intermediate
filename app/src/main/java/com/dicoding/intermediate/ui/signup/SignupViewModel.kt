package com.dicoding.intermediate.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate.data.UserRepository
import com.dicoding.intermediate.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registerResult = MutableStateFlow<RegisterResponse?>(null)
    val registerResult: StateFlow<RegisterResponse?> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.register(name, email, password)
            _registerResult.value = result
        }
    }
}
