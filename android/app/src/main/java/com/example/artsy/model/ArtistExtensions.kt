package com.example.artsy.model

fun Artist.toFavorite(): Favorite {
    return Favorite(
        id = this.id,
        title = this.title ?: "Unknown",
        date = null,
        thumbnail = this.thumbnail ?: "",
        birthYear = "",      // No value in Artist, send empty
        deathYear = "",      // No value in Artist, send empty
        nationality = ""     // No value in Artist, send empty
    )
}
