// File: viewmodel/ArtistArtworksViewModel.kt
package com.example.artsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy.model.Artwork
import com.example.artsy.model.Gene
import com.example.artsy.network.ArtsyApiService
import com.example.artsy.ui.state.ArtworkUiState
import com.example.artsy.ui.state.GeneDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArtistArtworksViewModel(
    private val apiService: ArtsyApiService,
    private val artistId: String
) : ViewModel() {

    private val _artworkState = MutableStateFlow(ArtworkUiState())
    val artworkState: StateFlow<ArtworkUiState> = _artworkState

    private val _geneDialogState = MutableStateFlow(GeneDialogState())
    val geneDialogState: StateFlow<GeneDialogState> = _geneDialogState

    init {
        loadArtworks()
    }

    private fun loadArtworks() {
        viewModelScope.launch {
            _artworkState.value = ArtworkUiState(isLoading = true)
            try {
                val artworks = apiService.getArtworksForArtist(artistId)
                _artworkState.value = ArtworkUiState(artworks = artworks)
            } catch (e: Exception) {
                _artworkState.value = ArtworkUiState(error = "Failed to load artworks.")
            }
        }
    }

    fun loadGenes(artworkId: String, artworkTitle: String) {
        viewModelScope.launch {
            _geneDialogState.value = GeneDialogState(isLoading = true, isVisible = true, currentArtworkTitle = artworkTitle)
            try {
                val genes = apiService.getGenesForArtwork(artworkId)
                _geneDialogState.value = GeneDialogState(genes = genes, isVisible = true, currentArtworkTitle = artworkTitle)
            } catch (e: Exception) {
                _geneDialogState.value = GeneDialogState(error = "Failed to load categories.", isVisible = true, currentArtworkTitle = artworkTitle)
            }
        }
    }

    fun dismissGeneDialog() {
        _geneDialogState.value = GeneDialogState()
    }
}
