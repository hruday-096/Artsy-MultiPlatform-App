package com.example.artsy.network

import retrofit2.http.*
import retrofit2.Response
import com.example.artsy.model.*

interface ArtsyApiService {

    @GET("/api/search/{term}")
    suspend fun searchArtists(
        @Path("term") query: String
    ): Response<List<Artist>>

    @POST("/api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>


    @GET("/api/favorites")
    suspend fun getFavorites(): Response<List<Favorite>>

    @POST("/api/favorites")
    suspend fun addFavorite(@Body favorite: Favorite): Response<Map<String, String>>

    @DELETE("/api/favorites/{id}")
    suspend fun removeFavorite(@Path("id") id: String): Response<Map<String, String>>


    @GET("api/artistdetails/{id}")
    suspend fun getArtistDetails(
        @Path("id") id: String
    ): Response<ArtistDetails>

    @GET("api/artworks/{artistId}")
    suspend fun getArtworksForArtist(@Path("artistId") artistId: String): List<Artwork>

    @GET("api/genes/{artworkId}")
    suspend fun getGenesForArtwork(@Path("artworkId") artworkId: String): List<Gene>

    @POST("/api/auth/logout")
    suspend fun logout(): Response<Unit>

    @DELETE("/api/auth/delete-account")
    suspend fun deleteAccount(): Response<Unit>

    @GET("api/similar/{id}")
    suspend fun getSimilarArtists(@Path("id") id: String): List<SimilarArtist>

    @GET("/api/auth/me")
    suspend fun getMe(): Response<MeResponse>

}
