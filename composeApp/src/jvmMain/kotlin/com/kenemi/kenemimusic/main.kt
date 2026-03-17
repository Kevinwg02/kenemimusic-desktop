package com.kenemi.kenemimusic

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Dimension

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

    val windowState = rememberWindowState(size = DpSize(1100.dp, 700.dp))
    val minWidth = 355.dp
    val minHeight = 500.dp

    Window(
        onCloseRequest = {
            playerController.release()
            FavoritesManager.save(favoritesState.favoriteIds)
            PlaylistsManager.save(playlistsState.playlists)
            exitApplication()
        },
        title = "Kenemi Music",
        state = windowState,
        undecorated = true,
        icon = painterResource("KM-icon.ico")
    ) {
        val density = LocalDensity.current
        SideEffect {
            window.minimumSize = with(density) {
                Dimension(minWidth.toPx().toInt(), minHeight.toPx().toInt())
            }
        }

        val isMaximized = windowState.placement == WindowPlacement.Maximized

        // KenemiMusicTheme doit entourer la TitleBar aussi
        App(
            isDesktop = true,
            library = library,
            playerState = playerState,
            playerController = playerController,
            favoritesState = favoritesState,
            playlistsState = playlistsState,
            statsState = statsState,
            initialDarkTheme = SettingsManager.isDarkTheme,
            titleBar = {
                CustomTitleBar(
                    onMinimize = { windowState.isMinimized = true },
                    onMaximize = {
                        windowState.placement = if (isMaximized)
                            WindowPlacement.Floating else WindowPlacement.Maximized
                    },
                    onClose = {
                        playerController.release()
                        FavoritesManager.save(favoritesState.favoriteIds)
                        PlaylistsManager.save(playlistsState.playlists)
                        exitApplication()
                    },
                    isMaximized = isMaximized
                )
            }
        )
    }
}