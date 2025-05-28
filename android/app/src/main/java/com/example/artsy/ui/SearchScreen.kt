package com.example.artsy.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.artsy.viewmodel.SearchViewModel
import com.example.artsy.ui.components.ArtistCard
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import com.example.artsy.model.toFavorite
import com.example.artsy.network.RetrofitInstance
import com.example.artsy.viewmodel.SessionViewModel
import kotlinx.coroutines.launch
import com.example.artsy.model.Favorite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    sessionViewModel: SessionViewModel
) {
    val viewModel: SearchViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val user by sessionViewModel.user.collectAsState()
    val isLoggedIn = user != null

    val favoriteMap = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(user) {
        if (user != null) {
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

    val query = remember { mutableStateOf("") }
    val artists by viewModel.artists.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        TextField(
                            value = query.value,
                            onValueChange = {
                                query.value = it
                                if (it.length >= 3) {
                                    viewModel.onQueryChanged(it)
                                } else {
                                    viewModel.onQueryChanged("")
                                }
                            },
                            placeholder = { Text("Search artists...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (query.value.isBlank()) {
                                        navController.popBackStack()
                                    } else {
                                        query.value = ""
                                        viewModel.onQueryChanged("")
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {
                items(artists, key = { it.id }) { artist ->
                    val isFavorited = favoriteMap[artist.id] == true

                    ArtistCard(
                        artist = artist,
                        isFavorited = isFavorited,
                        isLoggedIn = isLoggedIn,
                        onClick = {
                            navController.navigate("artistDetails/${artist.id}")
                        },
                        onToggleFavorite = {
                            Log.d("ToggleDebug", "Clicked star for ${artist.title} - was favorited: $isFavorited")
                            Log.d("ToggleDebug", "favoriteMap = ${favoriteMap.toMap()}")

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
                                                    birthYear = details?.birthday?.takeIf { it != "N/A" && it.isNotBlank() } ?: "",
                                                    deathYear = details?.deathday?.takeIf { it != "N/A" && it.isNotBlank() } ?: "",
                                                    nationality = details?.nationality?.takeIf { it != "N/A" && it.isNotBlank() } ?: ""
                                                )
                                            )
                                        } else {
                                            Log.e("AddFavorite", "Failed to fetch artist details: ${detailsResponse.code()}")
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

            if (artists.isEmpty() && query.value.length >= 3) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No artists found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
