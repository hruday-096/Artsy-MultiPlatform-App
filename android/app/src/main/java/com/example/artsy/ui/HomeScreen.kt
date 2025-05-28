package com.example.artsy.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.artsy.viewmodel.SessionViewModel
import com.example.artsy.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.navigation.NavHostController
import com.example.artsy.ui.components.FavoritesSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onSearchClick: () -> Unit,
    onLoginClick: () -> Unit,
    sessionViewModel: SessionViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val currentDate = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    val user by sessionViewModel.user.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Artist Search",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                actions = {
                    IconButton(onClick = { onSearchClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    var expanded by remember { mutableStateOf(false) }

                    if (user != null) {
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                AsyncImage(
                                    model = user!!.avatar,
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Log Out") },
                                    onClick = {
                                        expanded = false
                                        coroutineScope.launch {
                                            RetrofitInstance.api.logout()
                                            sessionViewModel.logout()
                                            snackbarHostState.showSnackbar("Logged out successfully")
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Account", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        expanded = false
                                        coroutineScope.launch {
                                            RetrofitInstance.api.deleteAccount()
                                            sessionViewModel.logout()
                                            snackbarHostState.showSnackbar("Account deleted successfully")
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        IconButton(onClick = { onLoginClick() }) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "User",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
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
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Date
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            // Favorites header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (user == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { onLoginClick() }) {
                        Text("Log in to see favorites")
                    }
                }
            } else {
                FavoritesSection(
                    onArtistClick = { artistId ->
                        navController.navigate("artistDetails/$artistId")
                    }
                )
            }

            // Powered by Artsy link
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Powered by Artsy",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, "https://www.artsy.net/".toUri())
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}
