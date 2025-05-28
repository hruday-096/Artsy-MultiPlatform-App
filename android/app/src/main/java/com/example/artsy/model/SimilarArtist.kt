package com.example.artsy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimilarArtist(
    val id: String,
    val name: String,
    val thumbnail: String
)
