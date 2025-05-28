package com.example.artsy.model

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class RegisterRequest(
    val fullname: String,
    val email: String,
    val password: String
)
