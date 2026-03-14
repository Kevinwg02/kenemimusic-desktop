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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlbumDetailScreen(albumId: Long, onBack: () -> Unit) {
    val library = LocalMusicLibrary.current
    val actions = LocalPlayerActions.current
    val playerState = LocalPlayerState.current

    val album = remember(albumId, library.albums) {
        library.albums.firstOrNull { it.id == albumId }
    }
    var coverUrl by remember(albumId) { mutableStateOf<String?>(null) }
    LaunchedEffect(albumId) {
        album?.let { coverUrl = ImageService.getAlbumCoverUrl(it.name, it.artist) }
    }

    val albumSongs = remember(albumId, library.songs) {
        library.songs
            .filter { it.album == album?.name && it.artist == album.artist }
            .sortedBy { it.title.lowercase() }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // ── En-tête ──
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bouton retour
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Previous, contentDescription = "Retour",
                    tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
            }

            // Pochette miniature
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    url = coverUrl,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = { if (album != null) AlbumCoverPlaceholder(album = album) }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = album?.name ?: "Album inconnu",
                    fontSize = 16.sp, fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${album?.artist ?: ""} • ${albumSongs.size} titre${if (albumSongs.size > 1) "s" else ""}",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Bouton tout jouer
            if (albumSongs.isNotEmpty()) {
                OutlinedButton(
                    onClick = { actions.playAll(albumSongs, 0) },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Tout jouer", fontSize = 12.sp)
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        // ── Liste des chansons ──
        if (albumSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune chanson trouvée", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                albumSongs.forEachIndexed { index, song ->
                    SongRow(
                        index = index + 1,
                        song = song,
                        isPlaying = song.id == playerState.currentSong?.id,
                        onClick = { actions.playAll(albumSongs, index) }
                    )
                }
            }
        }
    }
}