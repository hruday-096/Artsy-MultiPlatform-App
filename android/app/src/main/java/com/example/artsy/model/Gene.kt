// File: model/Gene.kt
package com.example.artsy.model

import kotlinx.serialization.Serializable

@Serializable
data class Gene(
    val category: String,
    val thumbnail: String
)
