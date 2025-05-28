package com.example.artsy.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.artsy.viewmodel.ArtistDetailsViewModel
import com.example.artsy.viewmodel.ArtistDetailsViewModelFactory
import com.example.artsy.viewmodel.ArtistArtworksViewModel
import com.example.artsy.viewmodel.ArtistArtworksViewModelFactory
import com.example.artsy.model.Favorite
import com.example.artsy.viewmodel.SessionViewModel
import com.example.artsy.network.RetrofitInstance
import com.example.artsy.viewmodel.SimilarArtistsViewModel
import com.example.artsy.viewmodel.SimilarArtistsViewModelFactory
import com.example.artsy.ui.components.SimilarArtistsTab
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.PersonSearch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailsScreen(
    navController: NavController,
    artistId: String,
    sessionViewModel: SessionViewModel
) {
    val viewModel: ArtistDetailsViewModel = viewModel(factory = ArtistDetailsViewModelFactory(artistId))
    val artworkViewModel: ArtistArtworksViewModel = viewModel(factory = ArtistArtworksViewModelFactory(RetrofitInstance.api, artistId))
    val user by sessionViewModel.user.collectAsState()
    val isLoggedIn = user != null
    val coroutineScope = rememberCoroutineScope()

    val artistDetails by viewModel.artistDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var isFavorite by remember { mutableStateOf(false) }
    var favoriteChecked by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(artistDetails?.name ?: "Artist Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isLoggedIn && artistDetails != null) {
                        IconButton(onClick = {
                            val artist = artistDetails!!
                            isFavorite = !isFavorite

                            coroutineScope.launch {
                                try {
                                    if (isFavorite) {
                                        RetrofitInstance.api.addFavorite(
                                            Favorite(
                                                id = artistId,
                                                title = artist.name,
                                                date = null,
                                                thumbnail = "/artsy_logo.svg",
                                                birthYear = artist.birthday.takeIf { it != "N/A" && it.isNotBlank() } ?: "",
                                                deathYear = artist.deathday.takeIf { it != "N/A" && it.isNotBlank() } ?: "",
                                                nationality = artist.nationality.takeIf { it != "N/A" && it.isNotBlank() } ?: ""
                                            )
                                        )
                                        snackbarHostState.showSnackbar("Added to Favorites")
                                    } else {
                                        RetrofitInstance.api.removeFavorite(artistId)
                                        snackbarHostState.showSnackbar("Removed from Favorites")
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error updating favorite")
                                    e.printStackTrace()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                                tint = Color.Black
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }

            artistDetails != null -> {
                LaunchedEffect(artistId, user) {
                    if (isLoggedIn && !favoriteChecked) {
                        try {
                            val favorites = RetrofitInstance.api.getFavorites()
                            if (favorites.isSuccessful) {
                                isFavorite = favorites.body()?.any { it.id == artistId } == true
                                favoriteChecked = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                var selectedTabIndex by remember { mutableStateOf(0) }
                val tabs = if (isLoggedIn) {
                    listOf(
                        "Details" to Icons.Default.Info,
                        "Artworks" to Icons.Default.AccountBox,
                        "Similar" to Icons.Default.PersonSearch
                    )
                } else {
                    listOf(
                        "Details" to Icons.Default.Info,
                        "Artworks" to Icons.Default.AccountBox
                    )
                }


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, (title, icon) ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) },
                                icon = { Icon(imageVector = icon, contentDescription = "$title Icon") }
                            )
                        }
                    }

                    when (tabs[selectedTabIndex].first) {
                        "Details" -> {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                artistDetails?.let { artist ->
                                    if (artist.name.isNotBlank()) {
                                        Text(
                                            text = artist.name,
                                            style = MaterialTheme.typography.headlineSmall,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }

                                    val dateInfo = listOfNotNull(
                                        artist.nationality.takeIf { it.isNotBlank() },
                                        artist.birthday.takeIf { it.isNotBlank() }?.let { birth ->
                                            artist.deathday.takeIf { it.isNotBlank() }?.let { "$birth - $it" } ?: birth
                                        }
                                    ).joinToString(", ")

                                    if (dateInfo.isNotBlank()) {
                                        Text(
                                            text = dateInfo,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                    }

                                    if (artist.biography.isNotBlank()) {
                                        artist.biography.trim().split("\n").forEach { paragraph ->
                                            if (paragraph.isNotBlank()) {
                                                Text(
                                                    text = paragraph.trim(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(bottom = 12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        "Artworks" -> ArtworksTab(viewModel = artworkViewModel)

                        "Similar" -> {
                            val similarViewModel: SimilarArtistsViewModel = viewModel(
                                factory = SimilarArtistsViewModelFactory(RetrofitInstance.api, artistId)
                            )

                            SimilarArtistsTab(
                                viewModel = similarViewModel,
                                sessionViewModel = sessionViewModel,
                                onArtistClick = { artistId: String ->
                                    navController.navigate("artistDetails/$artistId")
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}
