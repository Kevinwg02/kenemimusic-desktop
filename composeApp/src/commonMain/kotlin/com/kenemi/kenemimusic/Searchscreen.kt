package com.kenemi.kenemimusic

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen() {
    val library = LocalMusicLibrary.current
    val actions = LocalPlayerActions.current
    val playerState = LocalPlayerState.current
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Résultats
    val results = remember(query, library.songs) {
        if (query.isBlank()) SearchResults()
        else SearchResults(
            songs = library.songs.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true) ||
                        it.album.contains(query, ignoreCase = true)
            }.take(20),
            artists = library.artists.filter {
                it.name.contains(query, ignoreCase = true)
            }.take(10),
            albums = library.albums.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true)
            }.take(10)
        )
    }

    val navigate = LocalNavigate.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // Barre de recherche
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = Icons.Search, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Rechercher chansons, artistes, albums...") },
                singleLine = true,
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                )
            )
            if (query.isNotBlank()) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { query = "" },
                    contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Close, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp))
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        if (query.isBlank()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Search, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp))
                    Text("Recherchez dans votre bibliothèque", fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Aucun résultat pour \"$query\"", fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                // ── Chansons ──
                if (results.songs.isNotEmpty()) {
                    item {
                        SearchSectionHeader("Chansons", results.songs.size)
                    }
                    items(results.songs, key = { "song_${it.id}" }) { song ->
                        val index = library.songs.indexOf(song)
                        SongRow(
                            index = index + 1,
                            song = song,
                            isPlaying = song.id == playerState.currentSong?.id,
                            onClick = { actions.playAll(library.songs, index.coerceAtLeast(0)) }
                        )
                    }
                }

                // ── Artistes ──
                if (results.artists.isNotEmpty()) {
                    item { SearchSectionHeader("Artistes", results.artists.size) }
                    items(results.artists, key = { "artist_${it.id}" }) { artist ->
                        SearchArtistRow(artist = artist,
                            onClick = { navigate(Screen.ARTIST_DETAIL(artist.name)) })
                    }
                }

                // ── Albums ──
                if (results.albums.isNotEmpty()) {
                    item { SearchSectionHeader("Albums", results.albums.size) }
                    items(results.albums, key = { "album_${it.id}" }) { album ->
                        SearchAlbumRow(album = album,
                            onClick = { navigate(Screen.ALBUM_DETAIL(album.id)) })
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

data class SearchResults(
    val songs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList()
) {
    fun isEmpty() = songs.isEmpty() && artists.isEmpty() && albums.isEmpty()
}

@Composable
fun SearchSectionHeader(title: String, count: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.W600,
            color = MaterialTheme.colorScheme.onBackground)
        Text("$count résultat${if (count > 1) "s" else ""}", fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SearchArtistRow(artist: Artist, onClick: () -> Unit) {
    var imageUrl by remember(artist.name) { mutableStateOf<String?>(null) }
    LaunchedEffect(artist.name) { imageUrl = ImageService.getArtistImageUrl(artist.name) }

    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }
        .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center) {
            AsyncImage(url = imageUrl, modifier = Modifier.fillMaxSize(),
                placeholder = { ArtistInitials(name = artist.name) })
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(artist.name, fontSize = 13.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${artist.songCount} titres", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(imageVector = Icons.Next, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(14.dp))
    }
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).padding(start = 72.dp)
        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
}

@Composable
fun SearchAlbumRow(album: Album, onClick: () -> Unit) {
    var coverUrl by remember(album.id) { mutableStateOf<String?>(null) }
    LaunchedEffect(album.id) { coverUrl = ImageService.getAlbumCoverUrl(album.name, album.artist) }

    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }
        .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center) {
            AsyncImage(url = coverUrl, modifier = Modifier.fillMaxSize(),
                placeholder = { AlbumCoverPlaceholder(album = album) })
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(album.name, fontSize = 13.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(album.artist, fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(imageVector = Icons.Next, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(14.dp))
    }
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).padding(start = 72.dp)
        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
}