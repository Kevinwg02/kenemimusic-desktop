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
fun SongsScreen() {
    val library = LocalMusicLibrary.current
    val actions = LocalPlayerActions.current
    val playerState = LocalPlayerState.current

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // En-tête
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Toutes les chansons", fontSize = 14.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface)
            Text(text = "${library.songs.size} titres", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        when {
            library.isLoading -> Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            library.songs.isEmpty() -> Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Aucune chanson", fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Configurez votre dossier dans Paramètres", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
            else -> {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                    library.songs.forEachIndexed { index, song ->
                        SongRow(
                            index = index + 1,
                            song = song,
                            isPlaying = song.id == playerState.currentSong?.id,
                            onClick = { actions.playAll(library.songs, index) }
                        )
                    }
                }
            }
        }
    }
}