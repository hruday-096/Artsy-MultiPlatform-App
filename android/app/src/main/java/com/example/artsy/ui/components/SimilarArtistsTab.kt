package com.example.artsy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.artsy.model.Favorite
import com.example.artsy.viewmodel.SimilarArtistsViewModel
import com.example.artsy.model.SimilarArtist
import com.example.artsy.model.Artist
import com.example.artsy.network.RetrofitInstance
import com.example.artsy.viewmodel.SessionViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarArtistsTab(
    viewModel: SimilarArtistsViewModel,
    sessionViewModel: SessionViewModel,
    onArtistClick: (String) -> Unit
) {
    val artists by viewModel.similarArtists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val user by sessionViewModel.user.collectAsState()
    val isLoggedIn = user != null
    val favoriteMap = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(user) {
        if (isLoggedIn) {
            try {
                val response = RetrofitInstance.api.getFavorites()
                if (response.isSuccessful) {
                    val ids = response.body()?.map { it.id } ?: emptyList()
                    favoriteMap.clear()
                    ids.forEach { id -> favoriteMap[id] = true }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                    }
                }

                artists.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No similar artists found")
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(artists, key = { it.id }) { similarArtist ->
                            val isFavorited = favoriteMap[similarArtist.id] == true

                            val artist = Artist(
                                id = similarArtist.id,
                                title = similarArtist.name,
                                thumbnail = similarArtist.thumbnail
                            )

                            ArtistCard(
                                artist = artist,
                                isFavorited = isFavorited,
                                isLoggedIn = isLoggedIn,
                                onClick = { onArtistClick(artist.id) },
                                onToggleFavorite = {
                                    favoriteMap[artist.id] = !isFavorited

                                    coroutineScope.launch {
                                        try {
                                            if (!isFavorited) {
                                                val detailsResponse = RetrofitInstance.api.getArtistDetails(artist.id)
                                                if (detailsResponse.isSuccessful) {
                                                    val details = detailsResponse.body()
                                                    RetrofitInstance.api.addFavorite(
                                                        Favorite(
                                                            id = artist.id,
                                                            title = artist.title ?: "Unknown",
                                                            date = null,
                                                            thumbnail = artist.thumbnail ?: "/artsy_logo.svg",
                                                            birthYear = details?.birthday ?: "",
                                                            deathYear = details?.deathday ?: "",
                                                            nationality = details?.nationality ?: ""
                                                        )
                                                    )
                                                }
                                            } else {
                                                RetrofitInstance.api.removeFavorite(artist.id)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                },
                                snackbarHostState = snackbarHostState,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                }
            }
        }
    }
}
