package com.example.artsy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.artsy.viewmodel.ArtistArtworksViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun ArtworksTab(viewModel: ArtistArtworksViewModel) {
    val artworkState by viewModel.artworkState.collectAsState()
    val geneDialogState by viewModel.geneDialogState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            artworkState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            artworkState.error != null -> {
                Text(
                    text = artworkState.error ?: "",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            artworkState.artworks.isEmpty() -> {
                Text(
                    text = "No Artworks",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(artworkState.artworks) { artwork ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = artwork.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = artwork.date,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Image(
                                    painter = rememberAsyncImagePainter(artwork.thumbnail),
                                    contentDescription = artwork.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.loadGenes(artwork.id, artwork.title)
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("View categories")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (geneDialogState.isVisible) {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val genes = geneDialogState.genes

        AlertDialog(
            onDismissRequest = { viewModel.dismissGeneDialog() },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissGeneDialog() }) {
                    Text("Close")
                }
            },
            title = { Text("Categories", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                when {
                    geneDialogState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    geneDialogState.error != null -> {
                        Text("Error: ${geneDialogState.error}", color = MaterialTheme.colorScheme.onSurface)
                    }

                    geneDialogState.genes.isEmpty() -> {
                        Text("No Categories", color = MaterialTheme.colorScheme.onSurface)
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(370.dp)
                        ) {
                            LazyRow(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(genes.size) { index ->
                                    val gene = genes[index]
                                    Card(
                                        modifier = Modifier
                                            .width(260.dp)
                                            .fillMaxHeight(),
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(gene.thumbnail),
                                                contentDescription = gene.category,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(160.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = gene.category,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            val desc = getCategoryDescription(gene.category)
                                            if (desc.isNotBlank()) {
                                                Text(
                                                    text = desc,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val index = listState.firstVisibleItemIndex
                                        listState.animateScrollToItem((index - 1).coerceAtLeast(0))
                                    }
                                },
                                enabled = listState.firstVisibleItemIndex > 0,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                                    contentDescription = "Previous",
                                    tint = if (listState.firstVisibleItemIndex > 0)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val index = listState.firstVisibleItemIndex + 1
                                        listState.animateScrollToItem(index.coerceAtMost(genes.lastIndex))
                                    }
                                },
                                enabled = listState.firstVisibleItemIndex < genes.lastIndex,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowRight,
                                    contentDescription = "Next",
                                    tint = if (listState.firstVisibleItemIndex < genes.lastIndex)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

fun getCategoryDescription(category: String): String {
    return when (category) {
        "1860–1969" -> "Art, design, and architecture from 1860–1970."
        "19th Century" -> "Includes Impressionism and Romanticism."
        "En plein air" -> "Art painted outdoors."
        else -> "No description available."
    }
}
