package com.example.artsy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,

    @SerialName("name")
    val title: String? = null,

    val thumbnail: String? = null,
)

//{"id":"4d8b928b4eb68a1b2c0001f2","name":"Pablo Picasso","thumbnail":"https://d32dm0rphc51dk.cloudfront.net/i3rCA3IaKE-cLBnc-U5swQ/square.jpg"}