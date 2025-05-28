package com.example.artsy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeResponse(
    val user: RestoredUser
)

@Serializable
data class RestoredUser(
    val fullname: String,
    val email: String,
    @SerialName("profileImageUrl")
    val avatar: String
)
