package com.kenemi.kenemimusic

import androidx.compose.runtime.*

// =====================================================
// MODÈLES STATS
// =====================================================

data class SongStat(
    val song: Song,
    val playCount: Int,
    val totalMs: Long
)

data class ArtistStat(
    val artistName: String,
    val playCount: Int,
    val totalMs: Long
)

// =====================================================
// ÉTAT DES STATS (commonMain)
// =====================================================

class StatsState {
    var topSongs    by mutableStateOf<List<SongStat>>(emptyList())
    var topArtists  by mutableStateOf<List<ArtistStat>>(emptyList())
    var totalListeningMs by mutableStateOf(0L)
    var isLoaded    by mutableStateOf(false)

    fun compute(events: List<PlayEventData>, library: MusicLibrary) {
        if (events.isEmpty()) { isLoaded = true; return }

        // ── Top chansons ──
        val songGroups = events.groupBy { it.songId }
        topSongs = songGroups.mapNotNull { (songId, plays) ->
            val song = library.songs.firstOrNull { it.id == songId } ?: return@mapNotNull null
            SongStat(
                song = song,
                playCount = plays.size,
                totalMs = plays.sumOf { it.durationMs }
            )
        }.sortedByDescending { it.playCount }.take(10)

        // ── Top artistes (via parseArtists) ──
        val artistGroups = mutableMapOf<String, MutableList<PlayEventData>>()
        events.forEach { event ->
            val song = library.songs.firstOrNull { it.id == event.songId } ?: return@forEach
            parseArtists(song.artist).forEach { artist ->
                artistGroups.getOrPut(artist) { mutableListOf() }.add(event)
            }
        }
        topArtists = artistGroups.map { (name, plays) ->
            ArtistStat(
                artistName = name,
                playCount = plays.size,
                totalMs = plays.sumOf { it.durationMs }
            )
        }.sortedByDescending { it.playCount }.take(10)

        // ── Temps total ──
        totalListeningMs = events.sumOf { it.durationMs }
        isLoaded = true
    }
}

// Data class partagée commonMain/jvmMain via expect/actual
data class PlayEventData(val songId: Long, val durationMs: Long, val timestamp: Long)

val LocalStats = staticCompositionLocalOf<StatsState> {
    error("StatsState non fourni")
}