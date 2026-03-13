package com.kenemi.kenemimusic

import androidx.compose.runtime.*

// =====================================================
// ÉTAT DES PLAYLISTS (commonMain)
// =====================================================

class PlaylistsState {
    var playlists by mutableStateOf<List<Playlist>>(emptyList())

    fun create(name: String): Playlist {
        val newPlaylist = Playlist(
            id = System.currentTimeMillis(),
            name = name.trim()
        )
        playlists = playlists + newPlaylist
        return newPlaylist
    }

    fun rename(playlistId: Long, newName: String) {
        playlists = playlists.map {
            if (it.id == playlistId) it.copy(name = newName.trim()) else it
        }
    }

    fun delete(playlistId: Long) {
        playlists = playlists.filter { it.id != playlistId }
    }

    fun addSong(playlistId: Long, songId: Long) {
        playlists = playlists.map { playlist ->
            if (playlist.id == playlistId && songId !in playlist.songIds)
                playlist.copy(songIds = playlist.songIds + songId)
            else playlist
        }
    }

    fun removeSong(playlistId: Long, songId: Long) {
        playlists = playlists.map { playlist ->
            if (playlist.id == playlistId)
                playlist.copy(songIds = playlist.songIds - songId)
            else playlist
        }
    }

    fun getSongs(playlistId: Long, library: MusicLibrary): List<Song> {
        val playlist = playlists.firstOrNull { it.id == playlistId } ?: return emptyList()
        return playlist.songIds.mapNotNull { id -> library.songs.firstOrNull { it.id == id } }
    }
}

val LocalPlaylists = staticCompositionLocalOf<PlaylistsState> {
    error("PlaylistsState non fourni")
}