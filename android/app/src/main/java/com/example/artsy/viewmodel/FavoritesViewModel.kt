package com.example.artsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy.model.Favorite
import com.example.artsy.network.ArtsyApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class FavoritesViewModel : ViewModel() {

    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites

    private val api: ArtsyApiService = Retrofit.Builder()
        .baseUrl("https://artsyauth.uc.r.appspot.com/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
        )
        .build()
        .create(ArtsyApiService::class.java)

    init {
        fetchFavorites()
    }

    fun fetchFavorites() {
        viewModelScope.launch {
            try {
                val response = api.getFavorites()
                if (response.isSuccessful) {
                    _favorites.value = response.body() ?: emptyList()
                } else {
                    Log.e("FavoritesViewModel", "Failed to fetch: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun isFavorited(id: String): Boolean {
        return _favorites.value.any { it.id == id }
    }

    fun toggleFavorite(favorite: Favorite) {
        viewModelScope.launch {
            try {
                if (isFavorited(favorite.id)) {
                    api.removeFavorite(favorite.id)
                    _favorites.value = _favorites.value.filterNot { it.id == favorite.id }
                } else {
                    val response = api.addFavorite(favorite)
                    if (response.isSuccessful) {
                        _favorites.value = _favorites.value + favorite
                    } else {
                        Log.e("FavoritesViewModel", "Add failed: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Toggle error: ${e.message}")
            }
        }
    }
}
