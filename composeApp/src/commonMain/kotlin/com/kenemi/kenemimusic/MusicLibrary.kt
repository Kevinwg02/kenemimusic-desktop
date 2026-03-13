package com.kenemi.kenemimusic

import androidx.compose.runtime.*

class MusicLibrary {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var songs by mutableStateOf<List<Song>>(emptyList())
    var musicFolderPath by mutableStateOf("")

    val artists: List<Artist>
        get() {
            val artistSongMap = mutableMapOf<String, MutableList<Song>>()

            songs.forEach { song ->
                // Parser tous les artistes de la chanson (feat., &, x, etc.)
                val parsedArtists = parseArtists(song.artist)
                parsedArtists.forEach { artistName ->
                    val cleanName = artistName.trim()
                    if (cleanName.isNotBlank() && cleanName != "Artiste inconnu") {
                        artistSongMap.getOrPut(cleanName) { mutableListOf() }.add(song)
                    }
                }
            }

            return artistSongMap.map { (name, artistSongs) ->
                Artist(
                    id = name.hashCode().toLong(),
                    name = name,
                    songCount = artistSongs.size,
                )
            }.sortedBy { it.name.lowercase() }
        }

    val albums: List<Album>
        get() = songs
            .groupBy { "${it.album}|||${it.artist}" }
            .map { (_, albumSongs) ->
                val first = albumSongs.first()
                Album(
                    id = "${first.album}${first.artist}".hashCode().toLong(),
                    name = first.album,
                    artist = first.artist,
                    songCount = albumSongs.size,
                )
            }
            .sortedBy { it.name.lowercase() }

    fun onSongsLoaded(loadedSongs: List<Song>) {
        songs = loadedSongs
        isLoading = false
    }

    fun onScanStarted(folderPath: String) {
        musicFolderPath = folderPath
        isLoading = true
        errorMessage = null
    }

    fun onScanError(message: String) {
        errorMessage = message
        isLoading = false
    }
}

// =====================================================
// PARSER D'ARTISTES (commonMain)
// =====================================================

private val ARTIST_SEPARATORS = listOf(
    " feat. ", " feat ", " ft. ", " ft ",
    " featuring ", " & ", " x ", " X ",
    " vs. ", " vs ", " with ", ", ", " / ", "/"
)

fun parseArtists(artistField: String): List<String> {
    if (artistField.isBlank() || artistField == "Artiste inconnu") {
        return listOf(artistField)
    }

    // Chercher le premier séparateur présent
    for (sep in ARTIST_SEPARATORS) {
        val idx = artistField.indexOf(sep, ignoreCase = true)
        if (idx != -1) {
            val main = artistField.substring(0, idx).trim()
            val rest = artistField.substring(idx + sep.length)
                .removeSuffix(")").removePrefix("(").trim()

            val result = mutableListOf<String>()
            if (main.isNotBlank()) result.add(main)
            // Récursivement parser le reste (ex: "A feat. B & C")
            if (rest.isNotBlank()) result.addAll(parseArtists(rest))
            return result.distinct()
        }
    }

    return listOf(artistField.trim())
}

val LocalMusicLibrary = staticCompositionLocalOf<MusicLibrary> {
    error("MusicLibrary non fournie")
}