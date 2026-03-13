package com.kenemi.kenemimusic

import androidx.compose.runtime.*

// =====================================================
// ÉTAT DES FAVORIS (commonMain)
// =====================================================

class FavoritesState {
    var favoriteIds by mutableStateOf<Set<Long>>(emptySet())

    fun isFavorite(songId: Long) = songId in favoriteIds

    fun toggle(songId: Long): Boolean {
        favoriteIds = if (songId in favoriteIds) {
            favoriteIds - songId
        } else {
            favoriteIds + songId
        }
        return songId in favoriteIds
    }
}

val LocalFavorites = staticCompositionLocalOf<FavoritesState> {
    error("FavoritesState non fourni")
}