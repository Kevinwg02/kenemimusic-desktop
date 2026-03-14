package com.kenemi.kenemimusic

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

// =====================================================
// ÉCRAN PAROLES — Dialog qui s'ouvre par dessus le player
// =====================================================

@Composable
fun LyricsDialog(onDismiss: () -> Unit) {
    val playerState = LocalPlayerState.current
    val song = playerState.currentSong

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (song == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune chanson en cours", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LyricsContent(song = song, playerState = playerState, onDismiss = onDismiss)
            }
        }
    }
}

@Composable
fun LyricsContent(song: Song, playerState: PlayerStateHolder, onDismiss: () -> Unit) {
    var lyricsResult by remember { mutableStateOf<LyricsResult?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isSyncedMode by remember { mutableStateOf(true) }

    // Charger les paroles quand la chanson change
    LaunchedEffect(song.id) {
        isLoading = true
        error = null
        lyricsResult = null
        try {
            lyricsResult = LyricsService.getLyrics(
                title = song.title,
                artist = song.artist.split(Regex(" feat\\.| ft\\.| & ")).first().trim(),
                duration = song.duration
            )
            if (lyricsResult == null) error = "Paroles introuvables"
        } catch (e: Exception) {
            error = "Erreur : ${e.message}"
        }
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── En-tête ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onDismiss() },
                contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Close, contentDescription = "Fermer",
                    tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, fontSize = 14.sp, fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1)
                Text(song.artist, fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1)
            }
            // Toggle synced / plain (seulement si synced dispo)
            if (lyricsResult?.synced != null) {
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf("Sync" to true, "Texte" to false).forEach { (label, synced) ->
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (isSyncedMode == synced) MaterialTheme.colorScheme.primary
                                    else androidx.compose.ui.graphics.Color.Transparent
                                )
                                .clickable { isSyncedMode = synced }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(label, fontSize = 11.sp,
                                color = if (isSyncedMode == synced)
                                    androidx.compose.ui.graphics.Color.White
                                else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            // Source
            lyricsResult?.source?.let {
                Text(it, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        // ── Contenu ──
        when {
            isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text("Recherche des paroles...", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            error != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🎵", fontSize = 32.sp)
                    Text(error!!, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                }
            }
            lyricsResult != null -> {
                val result = lyricsResult!!
                if (isSyncedMode && result.synced != null) {
                    SyncedLyricsView(lines = result.synced, currentMs = playerState.currentMs)
                } else {
                    PlainLyricsView(text = result.plain)
                }
            }
        }
    }
}

// =====================================================
// VUE PAROLES SYNCHRONISÉES
// =====================================================

@Composable
fun SyncedLyricsView(lines: List<LyricLine>, currentMs: Long) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Trouver la ligne active
    val activeIndex = remember(currentMs) {
        var idx = 0
        for (i in lines.indices) {
            if (lines[i].timeMs <= currentMs) idx = i else break
        }
        idx
    }

    // Auto-scroll vers la ligne active
    LaunchedEffect(activeIndex) {
        scope.launch {
            if (activeIndex > 1) {
                listState.animateScrollToItem((activeIndex - 1).coerceAtLeast(0))
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(lines) { index, line ->
            val isActive = index == activeIndex
            val isPast = index < activeIndex

            AnimatedContent(targetState = isActive, label = "lyric_$index") { active ->
                Text(
                    text = line.text.ifBlank { "·" },
                    fontSize = if (active) 22.sp else 16.sp,
                    fontWeight = if (active) FontWeight.W600 else FontWeight.Normal,
                    color = when {
                        active -> MaterialTheme.colorScheme.primary
                        isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        else   -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    },
                    lineHeight = 28.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// =====================================================
// VUE PAROLES TEXTE BRUT
// =====================================================

@Composable
fun PlainLyricsView(text: String) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 26.sp
        )
    }
}