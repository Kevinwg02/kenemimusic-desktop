package com.kenemi.kenemimusic

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// =====================================================
// MODÈLES DEEZER
// =====================================================

@Serializable
data class DeezerArtistSearch(
    val data: List<DeezerArtist> = emptyList()
)

@Serializable
data class DeezerArtist(
    val id: Long = 0,
    val name: String = "",
    val picture: String = "",
    val picture_small: String = "",
    val picture_medium: String = "",
    val picture_big: String = "",
    val picture_xl: String = "",
)

@Serializable
data class DeezerAlbumSearch(
    val data: List<DeezerAlbum> = emptyList()
)

@Serializable
data class DeezerAlbum(
    val id: Long = 0,
    val title: String = "",
    val cover: String = "",
    val cover_small: String = "",
    val cover_medium: String = "",
    val cover_big: String = "",
    val cover_xl: String = "",
    val artist: DeezerArtistRef = DeezerArtistRef()
)

@Serializable
data class DeezerArtistRef(
    val name: String = ""
)

// =====================================================
// SERVICE
// =====================================================

object ImageService {

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // Cache en mémoire pour éviter de re-fetcher
    private val artistImageCache = mutableMapOf<String, String?>()
    private val albumCoverCache  = mutableMapOf<String, String?>()

    // ──────────────────────────────────────────
    // IMAGE ARTISTE
    // ──────────────────────────────────────────
    suspend fun getArtistImageUrl(artistName: String): String? {
        artistImageCache[artistName]?.let { return it }

        val result = try {
            val cleanName = cleanArtistName(artistName)
            val response: DeezerArtistSearch = client
                .get("https://api.deezer.com/search/artist?q=${cleanName.encodeUrl()}&limit=1")
                .body()
            response.data.firstOrNull()?.picture_big?.ifBlank { null }
        } catch (e: Exception) {
            null
        }

        artistImageCache[artistName] = result
        return result
    }

    // ──────────────────────────────────────────
    // POCHETTE ALBUM
    // ──────────────────────────────────────────
    suspend fun getAlbumCoverUrl(albumName: String, artistName: String): String? {
        val key = "$artistName|$albumName"
        albumCoverCache[key]?.let { return it }

        // 1. Chercher la pochette de l'album
        val albumResult = try {
            val cleanArtist = cleanArtistName(artistName)
            val query = "${albumName.encodeUrl()} ${cleanArtist.encodeUrl()}"
            val response: DeezerAlbumSearch = client
                .get("https://api.deezer.com/search/album?q=$query&limit=5")
                .body()
            val best = response.data.firstOrNull { album ->
                album.artist.name.equals(cleanArtist, ignoreCase = true) ||
                        album.title.equals(albumName, ignoreCase = true)
            } ?: response.data.firstOrNull()
            best?.cover_big?.ifBlank { null }
        } catch (e: Exception) { null }

        // 2. Fallback : image de l'artiste si pas de pochette
        val result = albumResult ?: getArtistImageUrl(artistName)

        albumCoverCache[key] = result
        return result
    }

    // Prend uniquement le premier artiste (avant feat., &, x, etc.)
    private fun cleanArtistName(artist: String): String {
        val separators = listOf(" feat. ", " feat ", " ft. ", " ft ",
            " featuring ", " & ", " x ", " X ", " vs. ", " vs ", " with ", " / ")
        var clean = artist
        for (sep in separators) {
            clean = clean.substringBefore(sep)
        }
        return clean.trim()
    }

    private fun String.encodeUrl() = java.net.URLEncoder.encode(this, "UTF-8")
}