package com.kenemi.kenemimusic

import java.io.File

// =====================================================
// FAVORIS — persistance locale (jvmMain)
// =====================================================

object FavoritesManager {

    private val file: File by lazy {
        val dir = File(System.getProperty("user.home"), ".kenemimusic")
        dir.mkdirs()
        File(dir, "favorites.txt")
    }

    fun load(): Set<Long> {
        if (!file.exists()) return emptySet()
        return file.readLines()
            .mapNotNull { it.trim().toLongOrNull() }
            .toSet()
    }

    fun save(ids: Set<Long>) {
        file.writeText(ids.joinToString("\n"))
    }
}