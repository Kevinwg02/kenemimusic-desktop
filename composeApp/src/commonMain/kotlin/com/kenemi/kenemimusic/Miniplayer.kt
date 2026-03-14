package com.kenemi.kenemimusic

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MiniPlayer(onPlayerClick: () -> Unit) {
    val state = LocalPlayerState.current
    val actions = LocalPlayerActions.current
    val favorites = LocalFavorites.current
    val song = state.currentSong ?: return

    var coverUrl by remember(song.id) { mutableStateOf<String?>(null) }
    LaunchedEffect(song.id) {
        coverUrl = ImageService.getAlbumCoverUrl(song.album, song.artist)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Barre de progression fine tout en haut
        Box(modifier = Modifier.fillMaxWidth().height(2.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)) {
            Box(modifier = Modifier
                .fillMaxWidth(state.progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .clickable { onPlayerClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pochette miniature
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    url = coverUrl,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = {
                        Icon(imageVector = Icons.Player, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp))
                    }
                )
            }

            // Titre + artiste
            Column(modifier = Modifier.weight(1f)) {
                Text(text = song.title, fontSize = 13.sp, fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = song.artist, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            // Favori
            IconButton(onClick = {
                favorites.toggle(song.id)
                saveFavorites(favorites.favoriteIds)
            }, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (favorites.isFavorite(song.id)) Icons.HeartFilled else Icons.Heart,
                    contentDescription = null,
                    tint = if (favorites.isFavorite(song.id)) KenemiColors.Favorite
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Previous
            IconButton(onClick = { actions.previous() }, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Previous, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
            }

            // Play / Pause
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { actions.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Pause else Icons.Play,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Next
            IconButton(onClick = { actions.next() }, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Next, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
            }
        }
    }
}