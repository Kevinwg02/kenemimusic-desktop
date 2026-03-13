package com.kenemi.kenemimusic

import androidx.compose.runtime.*

// =====================================================
// DATA CLASS immuable (utilisée par l'UI)
// =====================================================

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val currentMs: Long = 0L,
    val isFavorite: Boolean = false,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,
)

// =====================================================
// STATE HOLDER mutable (Compose observable)
// =====================================================

class PlayerStateHolder {
    var currentSong by mutableStateOf<Song?>(null)
    var isPlaying by mutableStateOf(false)
    var progress by mutableStateOf(0f)
    var currentMs by mutableStateOf(0L)
    var isFavorite by mutableStateOf(false)
    var isShuffle by mutableStateOf(false)
    var isRepeat by mutableStateOf(false)
    var queue by mutableStateOf<List<Song>>(emptyList())

    fun toPlayerState() = PlayerState(
        currentSong = currentSong,
        isPlaying = isPlaying,
        progress = progress,
        currentMs = currentMs,
        isFavorite = isFavorite,
        isShuffle = isShuffle,
        isRepeat = isRepeat,
    )
}

val LocalPlayerState = staticCompositionLocalOf<PlayerStateHolder> {
    error("PlayerStateHolder non fourni")
}