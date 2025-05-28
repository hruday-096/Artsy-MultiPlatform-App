package com.example.artsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.artsy.network.ArtsyApiService

class SimilarArtistsViewModelFactory(
    private val api: ArtsyApiService,
    private val artistId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SimilarArtistsViewModel(api, artistId) as T
    }
}
