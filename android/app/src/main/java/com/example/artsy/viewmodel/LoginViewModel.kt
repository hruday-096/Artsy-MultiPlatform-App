package com.example.artsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy.model.LoginRequest
import com.example.artsy.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val sessionViewModel: SessionViewModel) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(LoginRequest(email, password))
                val body = response.body()

                if (response.isSuccessful && body?.user != null) {
                    sessionViewModel.login(body.user)
                    _loginSuccess.value = true
                }
                else {
                    _errorMessage.value = body?.message ?: "Email or password incorrect"
                }
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
