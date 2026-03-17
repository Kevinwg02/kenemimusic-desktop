package com.kenemi.kenemimusic

import java.io.File

// =====================================================
// ENREGISTREMENT DES ÉCOUTES (jvmMain)
// Format : songId|durationMs|timestamp
// =====================================================

object ListeningStats {

    private val file: File by lazy {
        val dir = File(System.getProperty("user.home"), ".kenemimusic")
        dir.mkdirs()
        File(dir, "stats.txt")
    }

    data class PlayEvent(
        val songId: Long,
        val durationMs: Long,
        val timestamp: Long
    )

    fun recordPlay(songId: Long, durationMs: Long) {
        file.appendText("$songId|$durationMs|${System.currentTimeMillis()}\n")
    }

    fun load(): List<PlayEvent> {
        if (!file.exists()) return emptyList()
        return file.readLines()
            .mapNotNull { line ->
                val parts = line.trim().split("|")
                if (parts.size < 3) return@mapNotNull null
                PlayEvent(
                    songId = parts[0].toLongOrNull() ?: return@mapNotNull null,
                    durationMs = parts[1].toLongOrNull() ?: return@mapNotNull null,
                    timestamp = parts[2].toLongOrNull() ?: return@mapNotNull null
                )
            }
    }

    fun clear() { file.writeText("") }
}