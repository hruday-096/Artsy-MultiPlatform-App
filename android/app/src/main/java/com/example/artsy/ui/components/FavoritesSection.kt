package com.example.artsy.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.artsy.model.Favorite
import com.example.artsy.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FavoritesSection(
    modifier: Modifier = Modifier,
    onArtistClick: (String) -> Unit = {}
) {
    var favorites by remember { mutableStateOf<List<Favorite>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.api.getFavorites()
                if (response.isSuccessful) {
                    favorites = response.body() ?: emptyList()
                    Log.d("FavoritesResponse", "Received: ${response.body()}")
                } else {
                    val errorText = response.errorBody()?.string() ?: "Unknown error"
                    error = "Failed to load favorites: $errorText"
                }
            } catch (e: Exception) {
                error = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Text("Error loading favorites: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }

        favorites.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No favorites", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { fav ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onArtistClick(fav.id) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side: name + subtitle
                        Column(modifier = Modifier.weight(1f)) {
                            Text(fav.title, style = MaterialTheme.typography.titleMedium)

                            val subtitle = listOfNotNull(
                                fav.nationality?.takeIf { it.isNotBlank() },
                                fav.birthYear?.takeIf { it.isNotBlank() }
                            ).joinToString(", ")

                            if (subtitle.isNotBlank()) {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Right side: time ago + arrow
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var now by remember { mutableStateOf(System.currentTimeMillis()) }

                            LaunchedEffect(Unit) {
                                while (true) {
                                    now = System.currentTimeMillis()
                                    kotlinx.coroutines.delay(1000)
                                }
                            }

                            val agoText = fav.addedAt?.let { timestamp ->
                                try {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                                        timeZone = TimeZone.getTimeZone("UTC")
                                    }
                                    val addedDate = formatter.parse(timestamp)
                                    addedDate?.let {
                                        val diffSec = (now - it.time) / 1000
                                        when {
                                            diffSec < 5 -> "Just now"
                                            diffSec < 60 -> "$diffSec sec ago"
                                            diffSec < 3600 -> "${diffSec / 60} min ago"
                                            diffSec < 86400 -> "${diffSec / 3600} hr ago"
                                            else -> "${diffSec / 86400} days ago"
                                        }
                                    } ?: ""
                                } catch (e: Exception) {
                                    ""
                                }
                            } ?: ""

                            Text(
                                text = agoText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Go to details",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // ✅ Always show "Powered by Artsy" at the bottom of the list
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Powered by Artsy",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    "https://www.artsy.net/".toUri()
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}
