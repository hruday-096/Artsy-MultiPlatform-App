package com.example.artsy.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoginResponse(
    val message: String,
    val user: UserInfo? = null
)

@kotlinx.serialization.Serializable
data class UserInfo(
    val fullname: String,
    val email: String,
    val avatar: String
)


//@kotlinx.serialization.Serializable
//data class UserInfo(
//    val fullname: String,
//    val email: String,
//    val profileImageUrl: String
//)

