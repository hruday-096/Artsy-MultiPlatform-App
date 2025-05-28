package com.example.artsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy.model.SimilarArtist // adjust if in different package
import com.example.artsy.network.ArtsyApiService   // your Retrofit interface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SimilarArtistsViewModel(private val api: ArtsyApiService, private val artistId: String) : ViewModel() {
    private val _similarArtists = MutableStateFlow<List<SimilarArtist>>(emptyList())
    val similarArtists: StateFlow<List<SimilarArtist>> = _similarArtists

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            try {
                _similarArtists.value = api.getSimilarArtists(artistId)
            } catch (e: Exception) {
                Log.e("SimilarArtists", "Error loading similar artists", e)
                _error.value = "Failed to load similar artists"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
