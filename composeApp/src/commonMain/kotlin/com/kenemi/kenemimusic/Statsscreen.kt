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

@Composable
fun StatsScreen() {
    val stats = LocalStats.current
    val library = LocalMusicLibrary.current
    val actions = LocalPlayerActions.current

    // Recharger les stats à chaque ouverture
    LaunchedEffect(Unit) {
        val events = loadStats()
        stats.compute(events, library)
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // En-tête
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Statistiques", fontSize = 14.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface)
            if (stats.totalListeningMs > 0) {
                Text(
                    text = "Total : ${formatListeningTime(stats.totalListeningMs)}",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        when {
            !stats.isLoaded -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            stats.topSongs.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("", fontSize = 40.sp)
                    Text("Pas encore de statistiques", fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Écoute de la musique pour voir tes stats !",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
            else -> {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
                    .padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {

                    // ── Top Chansons ──
                    StatsSection(title = "🎵 Top 10 Chansons") {
                        val maxCount = stats.topSongs.maxOf { it.playCount }.coerceAtLeast(1)
                        stats.topSongs.forEachIndexed { index, stat ->
                            SongStatRow(
                                rank = index + 1,
                                stat = stat,
                                maxCount = maxCount,
                                onClick = { actions.play(stat.song) }
                            )
                        }
                    }

                    // ── Top Artistes ──
                    StatsSection(title = "🎤 Top 10 Artistes") {
                        val maxCount = stats.topArtists.maxOf { it.playCount }.coerceAtLeast(1)
                        stats.topArtists.forEachIndexed { index, stat ->
                            ArtistStatRow(
                                rank = index + 1,
                                stat = stat,
                                maxCount = maxCount
                            )
                        }
                    }

                    // ── Temps moyen par chanson ──
                    if (stats.topSongs.isNotEmpty()) {
                        StatsSection(title = "⏱ Moyenne par écoute") {
                            val avg = stats.totalListeningMs /
                                    stats.topSongs.sumOf { it.playCount }.coerceAtLeast(1)
                            StatCard {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text("Durée moyenne d'écoute",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface)
                                    Text(formatDuration(avg),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W600,
                                        color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            StatCard {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text("Écoutes enregistrées",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface)
                                    Text("${stats.topSongs.sumOf { it.playCount }}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W600,
                                        color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================
// COMPOSANTS
// =====================================================

@Composable
fun StatsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.W600,
            color = MaterialTheme.colorScheme.onBackground)
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface).padding(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                content()
            }
        }
    }
}

@Composable
fun StatCard(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp)) {
        content()
    }
}

@Composable
fun SongStatRow(rank: Int, stat: SongStat, maxCount: Int, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Rang
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                Text("$rank", fontSize = 12.sp, fontWeight = FontWeight.W600,
                    color = if (rank <= 3) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stat.song.title, fontSize = 13.sp, fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(stat.song.artist, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("${stat.playCount} écoute${if (stat.playCount > 1) "s" else ""}",
                fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // Barre de progression
        Box(modifier = Modifier.fillMaxWidth().padding(start = 34.dp).height(3.dp)
            .clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Box(modifier = Modifier
                .fillMaxWidth(stat.playCount.toFloat() / maxCount)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(
                    if (rank <= 3) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ))
        }
    }
}

@Composable
fun ArtistStatRow(rank: Int, stat: ArtistStat, maxCount: Int) {
    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                Text("$rank", fontSize = 12.sp, fontWeight = FontWeight.W600,
                    color = if (rank <= 3) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(stat.artistName, fontSize = 13.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${stat.playCount} écoute${if (stat.playCount > 1) "s" else ""}",
                fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(modifier = Modifier.fillMaxWidth().padding(start = 34.dp).height(3.dp)
            .clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Box(modifier = Modifier
                .fillMaxWidth(stat.playCount.toFloat() / maxCount)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(
                    if (rank <= 3) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ))
        }
    }
}

fun formatListeningTime(ms: Long): String {
    val totalMin = ms / 60_000
    val hours = totalMin / 60
    val mins = totalMin % 60
    return if (hours > 0) "${hours}h ${mins}min" else "${mins}min"
}