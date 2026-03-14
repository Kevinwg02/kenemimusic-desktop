package com.kenemi.kenemimusic

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =====================================================
// ÉCRAN ARTISTES
// =====================================================

@Composable
fun ArtistsScreen(onArtistClick: (String) -> Unit = {}) {
    val library = LocalMusicLibrary.current
    val artists = library.artists

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // En-tête
        ScreenHeader(title = "Artistes", count = "${artists.size} artistes")

        // Grille
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val columns = when {
                maxWidth < 500.dp -> 2
                maxWidth < 700.dp -> 3
                maxWidth < 900.dp -> 4
                else -> 5
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(artists) { artist ->
                    ArtistBubble(
                        artist = artist,
                        onClick = { onArtistClick(artist.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistBubble(
    artist: Artist,
    onClick: () -> Unit
) {
    var imageUrl by remember(artist.name) { mutableStateOf<String?>(null) }

    LaunchedEffect(artist.name) {
        imageUrl = ImageService.getArtistImageUrl(artist.name)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                url = imageUrl,
                modifier = Modifier.fillMaxSize(),
                placeholder = { ArtistInitials(name = artist.name) }
            )
        }

        // Nom de l'artiste
        Text(
            text = artist.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Nombre de chansons
        Text(
            text = "${artist.songCount} titres",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Initiales de l'artiste dans la bulle (placeholder)
@Composable
fun ArtistInitials(name: String) {
    val initials = name
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    // Couleur de fond unique basée sur le nom
    val bgColor = remember(name) {
        val colors = listOf(
            KenemiColors.Purple400,
            KenemiColors.Purple600,
        )
        colors[name.length % colors.size]
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 20.sp,
            fontWeight = FontWeight.W500,
            color = androidx.compose.ui.graphics.Color.White
        )
    }
}

// =====================================================
// ÉCRAN ALBUMS
// =====================================================

@Composable
fun AlbumsScreen(onAlbumClick: (Long) -> Unit = {}) {
    val library = LocalMusicLibrary.current
    val albums = library.albums

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(title = "Albums", count = "${albums.size} albums")

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(albums) { album ->
                AlbumCard(
                    album = album,
                    onClick = { onAlbumClick(album.id) }
                )
            }
        }
    }
}

@Composable
fun AlbumCard(
    album: Album,
    onClick: () -> Unit
) {
    var coverUrl by remember(album.id) { mutableStateOf<String?>(null) }
    LaunchedEffect(album.id) {
        coverUrl = ImageService.getAlbumCoverUrl(album.name, album.artist)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Pochette grande, carrée, coins arrondis en haut
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                url = coverUrl,
                modifier = Modifier.fillMaxSize(),
                placeholder = { AlbumCoverPlaceholder(album = album) }
            )
        }

        // Infos sous la pochette
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = album.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = album.artist,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    if (album.year > 0) { append(album.year); append(" • ") }
                    append("${album.songCount} titre${if (album.songCount > 1) "s" else ""}")
                },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

// Placeholder pochette album
@Composable
fun AlbumCoverPlaceholder(album: Album) {
    val bgColor = remember(album.name) {
        val colors = listOf(
            KenemiColors.Purple400,
            KenemiColors.Purple600,
        )
        colors[album.name.length % colors.size]
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Albums,
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
    }
}

// =====================================================
// EN-TÊTE COMMUN AUX ÉCRANS
// =====================================================

@Composable
fun ScreenHeader(title: String, count: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = count,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}