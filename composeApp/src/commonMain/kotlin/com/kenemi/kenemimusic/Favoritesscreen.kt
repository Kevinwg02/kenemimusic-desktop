package com.kenemi.kenemimusic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FavoritesScreen() {
    val library = LocalMusicLibrary.current
    val favorites = LocalFavorites.current
    val actions = LocalPlayerActions.current
    val playerState = LocalPlayerState.current

    val favoriteSongs = remember(favorites.favoriteIds, library.songs) {
        library.songs.filter { it.id in favorites.favoriteIds }
            .sortedBy { it.title.lowercase() }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Favoris", fontSize = 14.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface)
            Text("${favoriteSongs.size} titre${if (favoriteSongs.size > 1) "s" else ""}",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        if (favoriteSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Heart, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp))
                    Text("Aucun favori", fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Appuyez sur ♥ pour ajouter une chanson",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                favoriteSongs.forEachIndexed { index, song ->
                    SongRow(
                        index = index + 1,
                        song = song,
                        isPlaying = song.id == playerState.currentSong?.id,
                        onClick = { actions.playAll(favoriteSongs, index) }
                    )
                }
            }
        }
    }
}