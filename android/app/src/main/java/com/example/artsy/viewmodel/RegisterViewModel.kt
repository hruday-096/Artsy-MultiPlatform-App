package com.example.artsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy.model.RegisterRequest
import com.example.artsy.model.RegisterResponse
import com.example.artsy.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    fun register(fullname: String, email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.register(RegisterRequest(fullname, email, password))
                val body = response.body()

                if (response.isSuccessful && body?.user != null) {
                    _registerSuccess.value = true
                } else {
                    _errorMessage.value = body?.message ?: "Registration failed"
                }
            } catch (e: HttpException) {
                _errorMessage.value = "Email already registered"
            } catch (e: Exception) {
                _errorMessage.value = "Network error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearAuthError() {
        _errorMessage.value = null
    }

}
