package com.example.artsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy.model.Artist
import com.example.artsy.network.RetrofitInstance
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(300)                     // Wait 300ms after user stops typing
                .filter { it.length >= 3 }         // Only trigger for inputs 3+ chars
                .distinctUntilChanged()            // Ignore same input
                .collect { searchTerm ->
                    fetchArtists(searchTerm)
                }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    private suspend fun fetchArtists(term: String) {
        _isLoading.value = true
        _error.value = null
        try {
            val response = RetrofitInstance.api.searchArtists(term)
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                _artists.value = body

            } else {
                _error.value = "Error: ${response.code()}"
            }
        } catch (e: Exception) {
            _error.value = "Error: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }

}
