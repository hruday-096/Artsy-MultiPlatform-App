// File: viewmodel/ArtistArtworksViewModelFactory.kt
package com.example.artsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.artsy.network.ArtsyApiService

class ArtistArtworksViewModelFactory(
    private val apiService: ArtsyApiService,
    private val artistId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArtistArtworksViewModel(apiService, artistId) as T
    }
}