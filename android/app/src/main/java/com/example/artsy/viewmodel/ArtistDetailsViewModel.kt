package com.example.artsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy.model.ArtistDetails
import com.example.artsy.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArtistDetailsViewModel(
    private val artistId: String
) : ViewModel() {

    private val _artistDetails = MutableStateFlow<ArtistDetails?>(null)
    val artistDetails: StateFlow<ArtistDetails?> = _artistDetails

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchArtistDetails()
    }

    private fun fetchArtistDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = RetrofitInstance.api.getArtistDetails(artistId)
                if (response.isSuccessful) {
                    _artistDetails.value = response.body()
                } else {
                    _error.value = "Failed: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
