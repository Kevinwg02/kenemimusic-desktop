package com.kenemi.kenemimusic

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource

fun main() = application {
    val library = MusicLibrary()
    val playerState = PlayerStateHolder()
    val musicPlayer = MusicPlayer()
    val playerController = PlayerController(musicPlayer, playerState)
    val favoritesState = FavoritesState().also { it.favoriteIds = FavoritesManager.load() }
    val playlistsState = PlaylistsState().also { it.playlists = PlaylistsManager.load() }
    val statsState = StatsState()

    val savedFolder = SettingsManager.musicFolder
    if (savedFolder.isNotBlank()) {
        library.musicFolderPath = savedFolder
        CoroutineScope(Dispatchers.IO).launch {
            library.onScanStarted(savedFolder)
            try {
                val songs = SongScanner.scanFolder(savedFolder)
                library.onSongsLoaded(songs)
                playerState.queue = songs
            } catch (e: Exception) {
                library.onScanError("Erreur scan: ${e.message}")
            }
        }
    }

    Window(
        onCloseRequest = {
            playerController.release()
            FavoritesManager.save(favoritesState.favoriteIds)
            PlaylistsManager.save(playlistsState.playlists)
            exitApplication()
        },
        title = "Kenemi Music",
        state = rememberWindowState(size = DpSize(1100.dp, 700.dp)),
        icon = painterResource("KM-icon.ico")
    ) {
        App(
            isDesktop = true,
            library = library,
            playerState = playerState,
            playerController = playerController,
            favoritesState = favoritesState,
            playlistsState = playlistsState,
            statsState = statsState,
            initialDarkTheme = SettingsManager.isDarkTheme
        )
    }
}