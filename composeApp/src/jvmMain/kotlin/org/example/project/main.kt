package com.kenemi.kenemimusic

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() = application {
    val library = MusicLibrary()
    val playerState = PlayerStateHolder()
    val musicPlayer = MusicPlayer()
    val playerController = PlayerController(musicPlayer, playerState)

    // Charger les favoris
    val favoritesState = FavoritesState().also {
        it.favoriteIds = FavoritesManager.load()
    }

    // Observer les changements de favoris pour sauvegarder
    CoroutineScope(Dispatchers.IO).launch {
        // La sauvegarde se fait via onFavoriteToggle dans App
    }

    // Scan initial
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

    val windowState = rememberWindowState(size = DpSize(1100.dp, 700.dp))

    Window(
        onCloseRequest = {
            playerController.release()
            FavoritesManager.save(favoritesState.favoriteIds)
            exitApplication()
        },
        title = "Kenemi Music",
        state = windowState,
    ) {
        App(
            isDesktop = true,
            library = library,
            playerState = playerState,
            playerController = playerController,
            favoritesState = favoritesState,
            initialDarkTheme = SettingsManager.isDarkTheme
        )
    }
}