package com.example.artsy.model

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDetails(
    val name: String,
    val birthday: String,
    val deathday: String,
    val nationality: String,
    val biography: String
)
