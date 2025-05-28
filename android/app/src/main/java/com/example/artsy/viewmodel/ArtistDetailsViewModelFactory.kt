package com.example.artsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ArtistDetailsViewModelFactory(
    private val artistId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtistDetailsViewModel::class.java)) {
            return ArtistDetailsViewModel(artistId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
