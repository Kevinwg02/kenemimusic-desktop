package com.kenemi.kenemimusic

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ArtistDetailScreen(artistName: String, onBack: () -> Unit) {
    val library = LocalMusicLibrary.current
    val actions = LocalPlayerActions.current

    var artistImageUrl by remember(artistName) { mutableStateOf<String?>(null) }
    LaunchedEffect(artistName) {
        artistImageUrl = ImageService.getArtistImageUrl(artistName)
    }

    val artistSongs = remember(artistName, library.songs) {
        library.songs.filter { song ->
            parseArtists(song.artist).any { it.equals(artistName, ignoreCase = true) }
        }.sortedBy { it.title.lowercase() }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // En-tête
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Previous, contentDescription = "Retour",
                    tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    url = artistImageUrl,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = { ArtistInitials(name = artistName) }
                )
            }
            Column {
                Text(text = artistName, fontSize = 16.sp, fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(text = "${artistSongs.size} titre${if (artistSongs.size > 1) "s" else ""}",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        if (artistSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune chanson trouvée", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val playerState = LocalPlayerState.current
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                artistSongs.forEachIndexed { index, song ->
                    SongRow(
                        index = index + 1,
                        song = song,
                        isPlaying = song.id == playerState.currentSong?.id,
                        onClick = {
                            actions.playAll(artistSongs, index)
                        }
                    )
                }
            }
        }
    }
}