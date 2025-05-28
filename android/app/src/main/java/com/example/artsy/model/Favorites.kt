package com.example.artsy.model

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val id: String,
    val title: String,
    val date: String? = null,
    val thumbnail: String,
    val birthYear: String? = null,
    val deathYear: String? = null,
    val nationality: String? = null,
    val addedAt: String? = null
)
