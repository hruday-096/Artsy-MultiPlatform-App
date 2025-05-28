package com.example.artsy.ui.state

import com.example.artsy.model.Artwork
import com.example.artsy.model.Gene

data class ArtworkUiState(
    val artworks: List<Artwork> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)