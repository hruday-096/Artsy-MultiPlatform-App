package com.example.artsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy.model.UserInfo
import com.example.artsy.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class SessionViewModel : ViewModel() {

    private val _user = MutableStateFlow<UserInfo?>(null)
    val user: StateFlow<UserInfo?> = _user

    fun login(userInfo: UserInfo) {
        _user.value = userInfo
    }

    fun logout() {
        _user.value = null
    }

    fun isLoggedIn(): Boolean {
        return _user.value != null
    }

    fun restoreLoginIfSessionValid(onComplete: () -> Unit = {}) {
        Log.d("SessionRestore", "restoreLoginIfSessionValid() called")

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMe()
                Log.d("SessionRestore", "GET /me response code: ${response.code()}")

                if (response.isSuccessful) {
                    response.body()?.user?.let { restored ->
                        login(
                            UserInfo(
                                fullname = restored.fullname,
                                email = restored.email,
                                avatar = restored.avatar
                            )
                        )
                        Log.d("SessionRestore", "Restored user: ${restored.email}")
                    }
                } else {
                    Log.e("SessionRestore", "GET /me failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SessionRestore", "Exception during /me request", e)
            } finally {
                onComplete()
            }
        }
    }
}
