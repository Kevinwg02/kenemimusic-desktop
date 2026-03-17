package com.kenemi.kenemimusic

import javax.swing.JFileChooser
import javax.swing.UIManager

actual fun pickMusicFolder(currentPath: String): String? {
    return try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        val chooser = JFileChooser(currentPath.ifBlank { null }).apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = "Choisir le dossier musique"
            isAcceptAllFileFilterUsed = false
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            chooser.selectedFile.absolutePath
        else null
    } catch (e: Exception) { null }
}

actual fun scanMusicFolder(path: String): List<Song> = SongScanner.scanFolder(path)

actual fun saveMusicFolder(path: String) { SettingsManager.musicFolder = path }

actual fun saveFavorites(ids: Set<Long>) { FavoritesManager.save(ids) }

actual fun savePlaylists(playlists: List<Playlist>) { PlaylistsManager.save(playlists) }

actual fun loadStats(): List<PlayEventData> {
    return ListeningStats.load().map {
        PlayEventData(it.songId, it.durationMs, it.timestamp)
    }
}

actual fun clearImageMemoryCache() {
    clearAsyncImageCache()
}

actual fun getImageDiskBytes(url: String): ByteArray? = ImageDiskCache.get(url)
actual fun saveImageDiskBytes(url: String, bytes: ByteArray) = ImageDiskCache.put(url, bytes)
actual fun clearImageDiskCache() = ImageDiskCache.clear()
actual fun getImageDiskSizeKb(): Long = ImageDiskCache.sizeKb()
actual fun saveManualLyrics(artist: String, title: String, lyrics: String) = LyricsManager.save(artist, title, lyrics)
actual fun loadManualLyrics(artist: String, title: String): String? = LyricsManager.load(artist, title)