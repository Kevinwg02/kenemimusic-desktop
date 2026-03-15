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
import kotlinx.coroutines.launch

// =====================================================
// ÉCRAN ARTISTES
// =====================================================

@Composable
fun ArtistsScreen(onArtistClick: (String) -> Unit = {}) {
    val library = LocalMusicLibrary.current
    val artists = library.artists

    val grouped = remember(artists) {
        artists.groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }.toSortedMap()
    }
    val letters = grouped.keys.toList()
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader(title = "Artistes", count = "${artists.size} artistes")

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val columns = when {
                maxWidth < 500.dp -> 2
                maxWidth < 700.dp -> 3
                maxWidth < 900.dp -> 4
                else -> 10
            }

            val letterIndices = remember(grouped, columns) {
                var index = 0
                grouped.map { (letter, items) ->
                    val result = letter to index
                    index += items.size
                    result
                }.toMap()
            }

            Row(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    artists.forEach { artist ->
                        item(key = artist.id) {
                            ArtistBubble(artist = artist, onClick = { onArtistClick(artist.name) })
                        }
                    }
                }

                AlphabetBar(letters = letters, onLetterClick = { letter ->
                    val index = letterIndices[letter] ?: return@AlphabetBar
                    scope.launch { gridState.animateScrollToItem(index) }
                })
            }
        }
    }
}

@Composable
fun ArtistBubble(artist: Artist, onClick: () -> Unit) {
    var imageUrl by remember(artist.name) { mutableStateOf<String?>(null) }
    LaunchedEffect(artist.name) { imageUrl = ImageService.getArtistImageUrl(artist.name) }

    Column(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.aspectRatio(1f).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(url = imageUrl, modifier = Modifier.fillMaxSize(),
                placeholder = { ArtistInitials(name = artist.name) })
        }
        Text(text = artist.name, fontSize = 12.sp, fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(text = "${artist.songCount} titres", fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ArtistInitials(name: String) {
    val initials = name.split(" ").take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
    val bgColor = remember(name) {
        listOf(KenemiColors.Blue400, KenemiColors.Blue600)[name.length % 2]
    }
    Box(modifier = Modifier.fillMaxSize().background(bgColor),
        contentAlignment = Alignment.Center) {
        Text(text = initials, fontSize = 20.sp, fontWeight = FontWeight.W500,
            color = androidx.compose.ui.graphics.Color.White)
    }
}

// =====================================================
// ÉCRAN ALBUMS
// =====================================================

@Composable
fun AlbumsScreen(onAlbumClick: (Long) -> Unit = {}) {
    val library = LocalMusicLibrary.current
    val albums = library.albums

    val grouped = remember(albums) {
        albums.groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }.toSortedMap()
    }
    val letters = grouped.keys.toList()
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader(title = "Albums", count = "${albums.size} albums")

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val columns = when {
                maxWidth < 500.dp -> 2
                maxWidth < 700.dp -> 3
                maxWidth < 900.dp -> 4
                maxWidth < 1200.dp -> 6
                else -> 10
            }

            val letterIndices = remember(grouped, columns) {
                var index = 0
                grouped.map { (letter, items) ->
                    val result = letter to index
                    index += items.size
                    result
                }.toMap()
            }

            Row(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    albums.forEach { album ->
                        item(key = album.id) {
                            AlbumCard(album = album, onClick = { onAlbumClick(album.id) })
                        }
                    }
                }

                AlphabetBar(letters = letters, onLetterClick = { letter ->
                    val index = letterIndices[letter] ?: return@AlphabetBar
                    scope.launch { gridState.animateScrollToItem(index) }
                })
            }
        }
    }
}

@Composable
fun AlbumCard(album: Album, onClick: () -> Unit) {
    var coverUrl by remember(album.id) { mutableStateOf<String?>(null) }
    LaunchedEffect(album.id) { coverUrl = ImageService.getAlbumCoverUrl(album.name, album.artist) }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }.background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(url = coverUrl, modifier = Modifier.fillMaxSize(),
                placeholder = { AlbumCoverPlaceholder(album = album) })
        }
        Column(modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = album.name, fontSize = 12.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = album.artist, fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = buildString {
                if (album.year > 0) { append(album.year); append(" • ") }
                append("${album.songCount} titre${if (album.songCount > 1) "s" else ""}")
            }, fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun AlbumCoverPlaceholder(album: Album) {
    val bgColor = remember(album.name) {
        listOf(KenemiColors.Blue400, KenemiColors.Blue600)[album.name.length % 2]
    }
    Box(modifier = Modifier.fillMaxSize().background(bgColor),
        contentAlignment = Alignment.Center) {
        Icon(imageVector = Icons.Albums, contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp))
    }
}

// =====================================================
// BARRE ALPHABÉTIQUE
// =====================================================

@Composable
fun AlphabetBar(letters: List<Char>, onLetterClick: (Char) -> Unit) {
    val allLetters = listOf('#') + ('A'..'Z').toList()

    Column(
        modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        allLetters.forEach { letter ->
            val isAvailable = letter in letters
            Box(
                modifier = Modifier.size(18.dp).clip(CircleShape)
                    .then(if (isAvailable) Modifier.clickable { onLetterClick(letter) } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.toString(),
                    fontSize = 10.sp,
                    fontWeight = if (isAvailable) FontWeight.W600 else FontWeight.Normal,
                    color = if (isAvailable) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
}

// =====================================================
// EN-TÊTE COMMUN
// =====================================================

@Composable
fun ScreenHeader(title: String, count: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface)
            Text(text = count, fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))
    }
}