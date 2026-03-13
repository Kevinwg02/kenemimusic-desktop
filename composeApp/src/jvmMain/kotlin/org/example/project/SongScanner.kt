package com.kenemi.kenemimusic

import java.io.File

// =====================================================
// EXTENSIONS AUDIO SUPPORTÉES
// =====================================================

val SUPPORTED_EXTENSIONS = setOf(
    "mp3", "flac", "wav", "ogg", "m4a", "aac", "wma", "opus", "aiff", "alac"
)

// =====================================================
// SCANNER DE DOSSIER
// =====================================================

object SongScanner {

    /**
     * Scanne récursivement un dossier et retourne toutes les chansons trouvées
     */
    fun scanFolder(folderPath: String): List<Song> {
        val folder = File(folderPath)
        if (!folder.exists() || !folder.isDirectory) return emptyList()

        val songs = mutableListOf<Song>()
        var idCounter = 1L

        scanFolderRecursive(folder, songs, idCounter)

        return songs.sortedBy { it.title.lowercase() }
    }

    private fun scanFolderRecursive(
        folder: File,
        songs: MutableList<Song>,
        idCounter: Long
    ): Long {
        var currentId = idCounter

        folder.listFiles()?.sortedBy { it.name }?.forEach { file ->
            if (file.isDirectory) {
                currentId = scanFolderRecursive(file, songs, currentId)
            } else if (file.isAudioFile()) {
                val song = file.toSong(currentId)
                songs.add(song)
                currentId++
            }
        }

        return currentId
    }

    /**
     * Vérifie si un fichier est un fichier audio supporté
     */
    private fun File.isAudioFile(): Boolean {
        return extension.lowercase() in SUPPORTED_EXTENSIONS
    }

    /**
     * Convertit un fichier en Song en lisant ses métadonnées
     */
    private fun File.toSong(id: Long): Song {
        // Lire les tags ID3 si disponibles, sinon utiliser le nom de fichier
        val metadata = readAudioMetadata(this)

        return Song(
            id = id,
            title = metadata.title.ifBlank { nameWithoutExtension },
            artist = metadata.artist.ifBlank { parentFile?.parentFile?.name ?: "Artiste inconnu" },
            album = metadata.album.ifBlank { parentFile?.name ?: "Album inconnu" },
            duration = metadata.duration,
            filePath = absolutePath,
            albumArtUrl = null // chargé séparément via Deezer
        )
    }
}

// =====================================================
// MÉTADONNÉES AUDIO
// =====================================================

data class AudioMetadata(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: Long = 0L,
    val year: Int = 0,
    val trackNumber: Int = 0,
)

/**
 * Lit les métadonnées d'un fichier audio via JAudioTagger
 * Fallback sur le nom du fichier si impossible
 */
fun readAudioMetadata(file: File): AudioMetadata {
    return try {
        val audioFile = org.jaudiotagger.audio.AudioFileIO.read(file)
        val tag = audioFile.tag
        val header = audioFile.audioHeader

        AudioMetadata(
            title = tag?.getFirst(org.jaudiotagger.tag.FieldKey.TITLE) ?: "",
            artist = tag?.getFirst(org.jaudiotagger.tag.FieldKey.ARTIST) ?: "",
            album = tag?.getFirst(org.jaudiotagger.tag.FieldKey.ALBUM) ?: "",
            duration = (header?.trackLength?.toLong() ?: 0L) * 1000L,
            year = tag?.getFirst(org.jaudiotagger.tag.FieldKey.YEAR)?.toIntOrNull() ?: 0,
            trackNumber = tag?.getFirst(org.jaudiotagger.tag.FieldKey.TRACK)?.toIntOrNull() ?: 0,
        )
    } catch (e: Exception) {
        // Fallback : extraire infos du nom de fichier
        parseFromFileName(file)
    }
}

/**
 * Tente de parser titre/artiste depuis le nom du fichier
 * Formats courants : "Artiste - Titre.mp3" ou "Titre.mp3"
 */
private fun parseFromFileName(file: File): AudioMetadata {
    val name = file.nameWithoutExtension
    return if (name.contains(" - ")) {
        val parts = name.split(" - ", limit = 2)
        AudioMetadata(
            artist = parts[0].trim(),
            title = parts[1].trim(),
            album = file.parentFile?.name ?: ""
        )
    } else {
        AudioMetadata(
            title = name,
            album = file.parentFile?.name ?: ""
        )
    }
}