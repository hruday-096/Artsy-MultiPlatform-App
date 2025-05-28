package com.example.artsy.repository

import com.example.artsy.model.*
import com.example.artsy.network.RetrofitInstance
import retrofit2.Response

object ArtsyRepository {

    suspend fun searchArtists(query: String): Response<List<Artist>> {
        return RetrofitInstance.api.searchArtists(query)
    }

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return RetrofitInstance.api.login(request)
    }

    suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return RetrofitInstance.api.register(request)
    }

    suspend fun getFavorites(): Response<List<Favorite>> {
        return RetrofitInstance.api.getFavorites()
    }
}
