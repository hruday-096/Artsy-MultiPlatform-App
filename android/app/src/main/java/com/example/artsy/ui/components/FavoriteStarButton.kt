package com.example.artsy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun FavoriteStarButton(
    isFavorited: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleFavorite,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary) // Replaces hardcoded light blue
    ) {
        Icon(
            imageVector = if (isFavorited) Icons.Filled.Star else Icons.Outlined.StarOutline,
            contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
