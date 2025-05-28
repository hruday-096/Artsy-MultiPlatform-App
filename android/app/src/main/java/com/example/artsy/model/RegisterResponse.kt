package com.example.artsy.model

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class RegisterResponse(
    val message: String,
    val user: RegisteredUser? = null
)

@kotlinx.serialization.Serializable
data class RegisteredUser(
    val fullname: String,
    val email: String,
    val profileImageUrl: String
)

