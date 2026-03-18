package com.kenemi.kenemimusic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecentScreen() {
    val library = LocalMusicLibrary.current
    val actions = LocalPlayerActions.current
    val playerState = LocalPlayerState.current

    // Charger les écoutes récentes
    val recentSongs = remember(library.songs) {
        val events = loadStats()
        // Grouper par chanson, garder la dernière écoute, trier par timestamp desc
        events.groupBy { it.songId }
            .mapValues { (_, plays) -> plays.maxByOrNull { it.timestamp }!! }
            .entries
            .sortedByDescending { it.value.timestamp }
            .take(50)
            .mapNotNull { (songId, _) -> library.songs.firstOrNull { it.id == songId } }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Récemment écoutés", fontSize = 14.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface)
            Text("${recentSongs.size} titres", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        if (recentSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🎵", fontSize = 40.sp)
                    Text("Aucune écoute récente", fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Écoute des chansons pour les voir ici", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = recentSongs,
                    key = { _, song -> song.id }
                ) { index, song ->
                    SongRow(
                        index = index + 1,
                        song = song,
                        isPlaying = song.id == playerState.currentSong?.id,
                        onClick = { actions.playAll(recentSongs, index) }
                    )
                }
            }
        }
    }
}