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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =====================================================
// DÉTAIL D'UNE PLAYLIST
// =====================================================

@Composable
fun PlaylistDetailScreen(playlistId: Long, onBack: () -> Unit) {
    val playlists = LocalPlaylists.current
    val library = LocalMusicLibrary.current
    val actions = LocalPlayerActions.current
    val playerState = LocalPlayerState.current

    val playlist = playlists.playlists.firstOrNull { it.id == playlistId }
    val songs = remember(playlistId, playlists.playlists, library.songs) {
        playlists.getSongs(playlistId, library)
    }

    var showRenameDialog by remember { mutableStateOf(false) }
    var showAddSongsDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // ── En-tête ──
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onBack() },
                contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Previous, contentDescription = "Retour",
                    tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
            }

            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Playlists, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = playlist?.name ?: "Playlist",
                    fontSize = 16.sp, fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "${songs.size} titre${if (songs.size > 1) "s" else ""}",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Bouton renommer
            IconButton(onClick = { showRenameDialog = true }, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Settings, contentDescription = "Renommer",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }

            // Bouton tout jouer
            if (songs.isNotEmpty()) {
                OutlinedButton(
                    onClick = { actions.playAll(songs, 0) },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
                ) { Text("Tout jouer", fontSize = 12.sp) }
            }

            // Bouton ajouter des chansons
            OutlinedButton(
                onClick = { showAddSongsDialog = true },
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
            ) { Text("+ Ajouter", fontSize = 12.sp) }
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        // ── Liste des chansons ──
        if (songs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Playlist vide", fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Appuyez sur + Ajouter pour remplir la playlist",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                songs.forEachIndexed { index, song ->
                    PlaylistSongRow(
                        index = index + 1,
                        song = song,
                        isPlaying = song.id == playerState.currentSong?.id,
                        onPlay = { actions.playAll(songs, index) },
                        onRemove = {
                            playlist?.let {
                                playlists.removeSong(it.id, song.id)
                                savePlaylists(playlists.playlists)
                            }
                        }
                    )
                }
            }
        }
    }

    // ── Dialogue renommer ──
    if (showRenameDialog) {
        RenamePlaylistDialog(
            currentName = playlist?.name ?: "",
            onConfirm = { newName ->
                playlist?.let {
                    playlists.rename(it.id, newName)
                    savePlaylists(playlists.playlists)
                }
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    // ── Dialogue ajouter des chansons ──
    if (showAddSongsDialog) {
        AddSongsDialog(
            library = library,
            currentSongIds = playlist?.songIds ?: emptyList(),
            onAdd = { songId ->
                playlist?.let {
                    playlists.addSong(it.id, songId)
                    savePlaylists(playlists.playlists)
                }
            },
            onDismiss = { showAddSongsDialog = false }
        )
    }
}

// =====================================================
// SONG ROW avec bouton supprimer
// =====================================================

@Composable
fun PlaylistSongRow(index: Int, song: Song, isPlaying: Boolean, onPlay: () -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onPlay() }
            .background(if (isPlaying) MaterialTheme.colorScheme.surfaceVariant
            else androidx.compose.ui.graphics.Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.width(20.dp), contentAlignment = Alignment.Center) {
            if (isPlaying) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary))
            } else {
                Text("$index", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.title, fontSize = 13.sp,
                fontWeight = if (isPlaying) FontWeight.W500 else FontWeight.Normal,
                color = if (isPlaying) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "${song.artist} • ${song.album}", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(text = formatDuration(song.duration), fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(imageVector = Icons.Close, contentDescription = "Retirer",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp))
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))
}

// =====================================================
// DIALOGUE RENOMMER
// =====================================================

@Composable
fun RenamePlaylistDialog(currentName: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Renommer la playlist") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it },
                label = { Text("Nouveau nom") }, singleLine = true,
                modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank()) { Text("Renommer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

// =====================================================
// DIALOGUE AJOUTER DES CHANSONS
// =====================================================

@Composable
fun AddSongsDialog(
    library: MusicLibrary,
    currentSongIds: List<Long>,
    onAdd: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = remember(search, library.songs) {
        if (search.isBlank()) library.songs
        else library.songs.filter {
            it.title.contains(search, ignoreCase = true) ||
                    it.artist.contains(search, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter des chansons") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                OutlinedTextField(value = search, onValueChange = { search = it },
                    label = { Text("Rechercher") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
                    filtered.forEach { song ->
                        val already = song.id in currentSongIds
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable(enabled = !already) { onAdd(song.id) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = song.title, fontSize = 13.sp,
                                    color = if (already) MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(text = song.artist, fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (already) {
                                Text("✓", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer") }
        }
    )
}