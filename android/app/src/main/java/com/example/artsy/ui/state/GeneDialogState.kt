// File: ui/state/GeneDialogState.kt
package com.example.artsy.ui.state

import com.example.artsy.model.Gene

data class GeneDialogState(
    val genes: List<Gene> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVisible: Boolean = false,
    val currentArtworkTitle: String? = null
)
