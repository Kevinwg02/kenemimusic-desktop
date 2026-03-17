package com.kenemi.kenemimusic

import java.io.File

// =====================================================
// SAUVEGARDE DES PAROLES MANUELLES (jvmMain)
// =====================================================

object LyricsManager {

    private val dir: File by lazy {
        File(System.getProperty("user.home"), ".kenemimusic/lyrics").also { it.mkdirs() }
    }

    private fun fileFor(artist: String, title: String): File {
        val safe = { s: String -> s.replace(Regex("[^a-zA-Z0-9]"), "_").take(50) }
        return File(dir, "${safe(artist)}_${safe(title)}.txt")
    }

    fun save(artist: String, title: String, lyrics: String) {
        fileFor(artist, title).writeText(lyrics)
    }

    fun load(artist: String, title: String): String? {
        val file = fileFor(artist, title)
        return if (file.exists()) file.readText().takeIf { it.isNotBlank() } else null
    }
}