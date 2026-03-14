package com.kenemi.kenemimusic

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerScreenDesktop() {
    val state = LocalPlayerState.current
    val actions = LocalPlayerActions.current
    val library = LocalMusicLibrary.current
    val favorites = LocalFavorites.current
    val navigate = LocalNavigate.current
    var showLyrics by remember { mutableStateOf(false) }
    var coverUrl by remember(state.currentSong?.id) { mutableStateOf<String?>(null) }

    LaunchedEffect(state.currentSong?.id) {
        val song = state.currentSong ?: return@LaunchedEffect
        coverUrl = ImageService.getAlbumCoverUrl(song.album, song.artist)
    }

    if (showLyrics) {
        LyricsDialog(onDismiss = { showLyrics = false })
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Panneau gauche avec fond flouté
        Box(modifier = Modifier.width(340.dp).fillMaxHeight()) {
            // Image de fond floutée
            BlurredAsyncImage(
                url = coverUrl,
                modifier = Modifier.fillMaxSize(),
                blurRadius = 40f,
            )
            // Overlay sombre pour lisibilité
            Box(modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)))

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PlayerContent(
                    state = state.toPlayerState(),
                    isFavorite = state.currentSong?.id?.let { favorites.isFavorite(it) } ?: false,
                    onPlayPause = { actions.togglePlayPause() },
                    onNext = { actions.next() },
                    onPrevious = { actions.previous() },
                    onSeek = { actions.seekTo(it) },
                    onFavoriteToggle = {
                        state.currentSong?.id?.let { id ->
                            favorites.toggle(id)
                            saveFavorites(favorites.favoriteIds)
                        }
                    },
                    onShuffleToggle = { actions.toggleShuffle() },
                    onRepeatToggle = { actions.toggleRepeat() },
                    onLyricsClick = { showLyrics = true },
                    onCurrentSongClick = { navigate(Screen.CURRENT_QUEUE) },
                )
            } // Column
        } // Box fond flouté
        Box(modifier = Modifier.fillMaxHeight().width(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))
        SongListPanel(onSongClick = { song ->
            actions.playAll(library.songs,
                library.songs.indexOfFirst { it.id == song.id }.coerceAtLeast(0))
        })
    }
}

@Composable
fun PlayerScreenAndroid() {
    val state = LocalPlayerState.current
    val actions = LocalPlayerActions.current
    val favorites = LocalFavorites.current
    val navigate = LocalNavigate.current
    var showLyrics by remember { mutableStateOf(false) }

    if (showLyrics) {
        LyricsDialog(onDismiss = { showLyrics = false })
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PlayerContent(
            state = state.toPlayerState(),
            isFavorite = state.currentSong?.id?.let { favorites.isFavorite(it) } ?: false,
            onPlayPause = { actions.togglePlayPause() },
            onNext = { actions.next() },
            onPrevious = { actions.previous() },
            onSeek = { actions.seekTo(it) },
            onFavoriteToggle = {
                state.currentSong?.id?.let { id ->
                    favorites.toggle(id)
                    saveFavorites(favorites.favoriteIds)
                }
            },
            onShuffleToggle = { actions.toggleShuffle() },
            onRepeatToggle = { actions.toggleRepeat() },
            onLyricsClick = { showLyrics = true },
            onCurrentSongClick = { navigate(Screen.CURRENT_QUEUE) },
        )
    }
}

@Composable
fun PlayerContent(
    state: PlayerState,
    isFavorite: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onFavoriteToggle: () -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    onLyricsClick: () -> Unit,
    onCurrentSongClick: () -> Unit,
) {
    ArtistImage(imageUrl = state.currentSong?.albumArtUrl, size = 160.dp, isPlaying = state.isPlaying)
    Spacer(modifier = Modifier.height(20.dp))
    Text(text = state.currentSong?.title ?: "Aucune chanson",
        fontSize = 17.sp, fontWeight = FontWeight.W500,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1, overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = state.currentSong?.artist ?: "Sélectionnez une chanson",
        fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1, overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(20.dp))
    ActionButtons(
        isFavorite = isFavorite,
        onCurrentSongClick = onCurrentSongClick,
        onLyricsClick = onLyricsClick,
        onFavoriteToggle = onFavoriteToggle,
    )
    Spacer(modifier = Modifier.height(20.dp))
    ProgressBar(progress = state.progress, currentMs = state.currentMs,
        totalMs = state.currentSong?.duration ?: 0L, onSeek = onSeek)
    Spacer(modifier = Modifier.height(16.dp))
    PlayerControls(
        isPlaying = state.isPlaying, isShuffle = state.isShuffle, isRepeat = state.isRepeat,
        onPlayPause = onPlayPause, onNext = onNext, onPrevious = onPrevious,
        onShuffleToggle = onShuffleToggle, onRepeatToggle = onRepeatToggle,
    )
}

@Composable
fun ArtistImage(imageUrl: String?, size: Dp, isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart), label = "rotation"
    )
    val state = LocalPlayerState.current
    var coverUrl by remember(state.currentSong?.id) { mutableStateOf<String?>(null) }

    LaunchedEffect(state.currentSong?.id) {
        val song = state.currentSong ?: return@LaunchedEffect
        coverUrl = ImageService.getAlbumCoverUrl(song.album, song.artist)
    }

    Box(
        modifier = Modifier.size(size).clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(if (isPlaying) Modifier.rotate(rotation) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            url = coverUrl,
            modifier = Modifier.fillMaxSize(),
            placeholder = { PlaceholderArtImage(size) }
        )
    }
}

@Composable
fun PlaceholderArtImage(size: Dp) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxSize().clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant))
        Box(modifier = Modifier.size(size * 0.3f).clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface))
        Box(modifier = Modifier.size(8.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary))
    }
}

@Composable
fun ActionButtons(isFavorite: Boolean, onCurrentSongClick: () -> Unit,
                  onLyricsClick: () -> Unit, onFavoriteToggle: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        SmallIconButton(onClick = onCurrentSongClick) {
            Icon(imageVector = Icons.Songs, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
        OutlinedButton(
            onClick = onLyricsClick,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
            modifier = Modifier.height(32.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
        ) { Text(text = "Paroles", fontSize = 12.sp) }
        SmallIconButton(onClick = onFavoriteToggle, isActive = isFavorite, activeColor = KenemiColors.Favorite) {
            Icon(imageVector = if (isFavorite) Icons.HeartFilled else Icons.Heart,
                contentDescription = null,
                tint = if (isFavorite) KenemiColors.Favorite else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun ProgressBar(progress: Float, currentMs: Long, totalMs: Long, onSeek: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = formatDuration(currentMs), fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = formatDuration(totalMs), fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Slider(value = progress.coerceIn(0f, 1f), onValueChange = onSeek,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ))
    }
}

@Composable
fun PlayerControls(isPlaying: Boolean, isShuffle: Boolean, isRepeat: Boolean,
                   onPlayPause: () -> Unit, onNext: () -> Unit, onPrevious: () -> Unit,
                   onShuffleToggle: () -> Unit, onRepeatToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onShuffleToggle, modifier = Modifier.size(36.dp)) {
            Icon(imageVector = Icons.Shuffle, contentDescription = null,
                tint = if (isShuffle) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onPrevious, modifier = Modifier.size(44.dp)) {
            Icon(imageVector = Icons.Previous, contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
        }
        Box(modifier = Modifier.size(52.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary).clickable { onPlayPause() },
            contentAlignment = Alignment.Center) {
            Icon(imageVector = if (isPlaying) Icons.Pause else Icons.Play,
                contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        IconButton(onClick = onNext, modifier = Modifier.size(44.dp)) {
            Icon(imageVector = Icons.Next, contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
        }
        IconButton(onClick = onRepeatToggle, modifier = Modifier.size(36.dp)) {
            Icon(imageVector = Icons.Repeat, contentDescription = null,
                tint = if (isRepeat) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun SongListPanel(onSongClick: (Song) -> Unit = {}) {
    val library = LocalMusicLibrary.current
    val playerState = LocalPlayerState.current

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Toutes les chansons", fontSize = 14.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface)
            Text("${library.songs.size} titres", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        when {
            library.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            library.songs.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Aucune chanson", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Configurez votre dossier dans Paramètres", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
            else -> {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                    library.songs.forEachIndexed { index, song ->
                        SongRow(index = index + 1, song = song,
                            isPlaying = song.id == playerState.currentSong?.id,
                            onClick = { onSongClick(song) })
                    }
                }
            }
        }
    }
}

@Composable
fun SongRow(index: Int, song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
            .background(if (isPlaying) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.width(20.dp), contentAlignment = Alignment.Center) {
            if (isPlaying) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary))
            } else {
                Text(text = "$index", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.title, fontSize = 13.sp,
                fontWeight = if (isPlaying) FontWeight.W500 else FontWeight.Normal,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "${song.artist} • ${song.album}", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(text = formatDuration(song.duration), fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))
}

@Composable
fun SmallIconButton(onClick: () -> Unit, isActive: Boolean = false,
                    activeColor: Color = MaterialTheme.colorScheme.primary, content: @Composable () -> Unit) {
    Box(modifier = Modifier.size(32.dp).clip(CircleShape)
        .background(if (isActive) activeColor.copy(alpha = 0.12f)
        else MaterialTheme.colorScheme.surfaceVariant)
        .clickable(interactionSource = remember { MutableInteractionSource() },
            indication = null, onClick = onClick),
        contentAlignment = Alignment.Center) { content() }
}

fun formatDuration(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "$min:${sec.toString().padStart(2, '0')}"
}