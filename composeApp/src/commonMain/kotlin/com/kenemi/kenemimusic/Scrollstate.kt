package com.kenemi.kenemimusic

import androidx.compose.runtime.*

// =====================================================
// SAUVEGARDE DES POSITIONS DE SCROLL (commonMain)
// =====================================================

class ScrollPositionState {
    // Clé -> index de l'item visible en haut
    private val positions = mutableMapOf<String, Int>()

    fun save(key: String, index: Int) {
        positions[key] = index
    }

    fun restore(key: String): Int = positions[key] ?: 0
}

val LocalScrollPosition = staticCompositionLocalOf { ScrollPositionState() }