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
private const val USE_ARTIST_MARKER = "@artist"

data class ArtistCandidate(
    val id: Long,
    val name: String,
    val thumbUrl: String,   // pour la grille
    val imageUrl: String    // image sauvegardée
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

        val diskKey = "artist_${artistName.hashCode()}"
        loadImageUrl(diskKey)?.takeIf { it.isNotBlank() }?.let { url ->
            artistImageCache[artistName] = url
            return url
        }

        val result = try {
            val cleanName = cleanArtistName(artistName)
            val response: DeezerArtistSearch = client
                .get("https://api.deezer.com/search/artist?q=${cleanName.encodeUrl()}&limit=1")
                .body()
            response.data.firstOrNull()?.picture_big?.ifBlank { null }
        } catch (e: Exception) { null }

        artistImageCache[artistName] = result
        if (result != null) saveImageUrl(diskKey, result)
        return result
    }

    // ──────────────────────────────────────────
    // POCHETTE ALBUM
    // ──────────────────────────────────────────
    suspend fun getAlbumCoverUrl(albumName: String, artistName: String): String? {
        val key = "$artistName|$albumName"
        albumCoverCache[key]?.let { return it }

        val diskKey = "album_${key.hashCode()}"
        loadImageUrl(diskKey)?.takeIf { it.isNotBlank() }?.let { stored ->
            val url = if (stored == USE_ARTIST_MARKER)
                getArtistImageUrl(cleanArtistName(artistName))
            else stored
            if (url != null) {
                albumCoverCache[key] = url
                return url
            }
        }

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

        val result = albumResult ?: getArtistImageUrl(cleanArtistName(artistName))

        albumCoverCache[key] = result
        if (result != null) saveImageUrl(diskKey, result)
        return result
    }

    // ──────────────────────────────────────────
    // CHOIX MANUEL : ARTISTE
    // ──────────────────────────────────────────

    /** Propose les candidats les plus probables (nom exact en premier). */
    suspend fun searchArtistCandidates(query: String): List<ArtistCandidate> {
        val target = cleanArtistName(query)
        val names = listOf(target, query.trim()).filter { it.isNotBlank() }.distinct()
        val all = mutableListOf<DeezerArtist>()
        for (n in names) {
            try {
                val r: DeezerArtistSearch = client
                    .get("https://api.deezer.com/search/artist?q=${n.encodeUrl()}&limit=9")
                    .body()
                all += r.data
            } catch (_: Exception) { }
        }
        return all
            .distinctBy { it.id }
            .filter { it.picture_big.isNotBlank() && !it.picture_big.contains("/artist//") }
            .sortedByDescending { it.name.equals(target, ignoreCase = true) }
            .map { ArtistCandidate(it.id, it.name,
                thumbUrl = it.picture_medium.ifBlank { it.picture_big },
                imageUrl = it.picture_big) }
    }

    /** Fixe manuellement l'image d'un artiste (persistant). */
    fun setArtistImage(artistName: String, url: String) {
        artistImageCache[artistName] = url
        saveImageUrl("artist_${artistName.hashCode()}", url)
        albumCoverCache.clear()
    }
    /** Revient à la recherche automatique. */
    fun resetArtistImage(artistName: String) {
        artistImageCache.remove(artistName)
        saveImageUrl("artist_${artistName.hashCode()}", "")
        albumCoverCache.clear()
    }

    // ──────────────────────────────────────────
    // CHOIX MANUEL : ALBUM
    // ──────────────────────────────────────────

    /** L'album utilise l'image de l'artiste (suit les changements de l'artiste). */
    fun setAlbumUsesArtistImage(albumName: String, artistName: String) {
        val key = "$artistName|$albumName"
        albumCoverCache.remove(key)
        saveImageUrl("album_${key.hashCode()}", USE_ARTIST_MARKER)
    }

    /** Revient à la pochette automatique de l'album. */
    fun resetAlbumCover(albumName: String, artistName: String) {
        val key = "$artistName|$albumName"
        albumCoverCache.remove(key)
        saveImageUrl("album_${key.hashCode()}", "")
    }

    // ──────────────────────────────────────────
    // UTILITAIRES
    // ──────────────────────────────────────────

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