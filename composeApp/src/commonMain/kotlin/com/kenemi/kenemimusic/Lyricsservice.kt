package com.kenemi.kenemimusic

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// =====================================================
// MODÈLES
// =====================================================

data class LyricsResult(
    val plain: String,                    // texte brut
    val synced: List<LyricLine>? = null,  // paroles synchronisées (si dispo)
    val source: String = ""
)

data class LyricLine(
    val timeMs: Long,
    val text: String
)

@Serializable
data class LrcLibResponse(
    val id: Int? = null,
    val trackName: String? = null,
    val artistName: String? = null,
    val plainLyrics: String? = null,
    val syncedLyrics: String? = null,
)

// =====================================================
// SERVICE
// =====================================================

object LyricsService {

    private val lyricsCache = mutableMapOf<String, LyricsResult?>()

    fun clearCache() {
        lyricsCache.clear()
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // Nettoyer le nom d'artiste — garder seulement le premier
    private fun cleanArtist(artist: String): String {
        val separators = listOf(
            " feat. ", " feat ", " ft. ", " ft ",
            " featuring ", " & ", " x ", " X ",
            " vs. ", " vs ", " with ", " / ", "/"
        )
        var clean = artist
        for (sep in separators) {
            if (clean.contains(sep, ignoreCase = true)) {
                clean = clean.substringBefore(sep).trim()
                break
            }
        }
        return clean
    }

    suspend fun getLyrics(title: String, artist: String, duration: Long? = null): LyricsResult? {
        val clean = cleanArtist(artist)
        val key = "$clean|$title"
        lyricsCache[key]?.let { return it }

        // 1. Paroles manuelles sauvegardées
        val manual = loadManualLyrics(clean, title)
        if (manual != null) {
            val result = LyricsResult(plain = manual, source = "Manuel")
            lyricsCache[key] = result
            return result
        }

        // 2. LrcLib avec artiste propre + durée
        var result = tryLrcLib(title, clean, duration)

        // 3. Fallback : LrcLib sans durée
        if (result == null) result = tryLrcLib(title, clean, null)

        // 4. Fallback : LrcLib sans artiste (titre seul)
        if (result == null) result = tryLrcLib(title, "", null)

        lyricsCache[key] = result
        return result
    }

    fun saveManual(artist: String, title: String, lyrics: String) {
        val clean = cleanArtist(artist)
        val key = "$clean|$title"
        val result = LyricsResult(plain = lyrics, source = "Manuel")
        lyricsCache[key] = result
        saveManualLyrics(clean, title, lyrics)
    }

    // ──────────────────────────────────────────
    // LrcLib — supporte les paroles synchronisées
    // ──────────────────────────────────────────
    private suspend fun tryLrcLib(title: String, artist: String, duration: Long?): LyricsResult? {
        return try {
            val params = buildString {
                append("track_name=${title.encodeUrl()}")
                if (artist.isNotBlank()) append("&artist_name=${artist.encodeUrl()}")
                if (duration != null && duration > 0) append("&duration=${duration / 1000}")
            }
            val response: LrcLibResponse = client
                .get("https://lrclib.net/api/get?$params")
                .body()

            val plain = response.plainLyrics ?: return null
            val synced = response.syncedLyrics?.let { parseSyncedLyrics(it) }

            LyricsResult(plain = plain, synced = synced, source = "LrcLib")
        } catch (e: Exception) {
            null
        }
    }

    // ──────────────────────────────────────────
    // Lyrics.ovh — fallback texte brut
    // ──────────────────────────────────────────
    private suspend fun tryLyricsOvh(title: String, artist: String): LyricsResult? {
        return try {
            val response = client
                .get("https://api.lyrics.ovh/v1/${artist.encodeUrl()}/${title.encodeUrl()}")
                .bodyAsText()

            // Lyrics.ovh retourne {"lyrics":"..."} ou {"error":"..."}
            if (response.contains("\"error\"")) return null
            val lyrics = response
                .substringAfter("\"lyrics\":\"")
                .substringBefore("\"}")
                .replace("\\n", "\n")
                .replace("\\r", "")
                .trim()

            if (lyrics.isBlank()) null
            else LyricsResult(plain = lyrics, source = "Lyrics.ovh")
        } catch (e: Exception) {
            null
        }
    }

    // ──────────────────────────────────────────
    // Parser les paroles synchronisées LRC
    // Format : [mm:ss.xx] Texte de la ligne
    // ──────────────────────────────────────────
    private fun parseSyncedLyrics(lrc: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        val regex = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)")

        lrc.lines().forEach { line ->
            val match = regex.find(line.trim()) ?: return@forEach
            val (min, sec, cs, text) = match.destructured
            val ms = min.toLong() * 60_000 +
                    sec.toLong() * 1_000 +
                    cs.toLong() * (if (cs.length == 2) 10L else 1L)
            lines.add(LyricLine(timeMs = ms, text = text.trim()))
        }
        return lines
    }

    private fun String.encodeUrl() = java.net.URLEncoder.encode(this, "UTF-8")
}