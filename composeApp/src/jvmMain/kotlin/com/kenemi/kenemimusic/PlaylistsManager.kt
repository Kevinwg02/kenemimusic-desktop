package com.kenemi.kenemimusic

import java.io.File

// =====================================================
// PLAYLISTS — persistance locale (jvmMain)
// Format : fichier texte simple
// Une playlist par bloc séparé par ligne vide :
//   id|nom|timestamp
//   songId1
//   songId2
//   ...
// =====================================================

object PlaylistsManager {

    private val file: File by lazy {
        val dir = File(System.getProperty("user.home"), ".kenemimusic")
        dir.mkdirs()
        File(dir, "playlists.txt")
    }

    fun load(): List<Playlist> {
        if (!file.exists()) return emptyList()
        val playlists = mutableListOf<Playlist>()
        val blocks = file.readText().split("\n\n")
        for (block in blocks) {
            val lines = block.trim().lines().filter { it.isNotBlank() }
            if (lines.isEmpty()) continue
            val header = lines[0].split("|")
            if (header.size < 2) continue
            val id = header[0].toLongOrNull() ?: continue
            val name = header[1]
            val createdAt = header.getOrNull(2)?.toLongOrNull() ?: System.currentTimeMillis()
            val songIds = lines.drop(1).mapNotNull { it.trim().toLongOrNull() }
            playlists.add(Playlist(id = id, name = name, songIds = songIds, createdAt = createdAt))
        }
        return playlists
    }

    fun save(playlists: List<Playlist>) {
        val content = playlists.joinToString("\n\n") { playlist ->
            buildString {
                appendLine("${playlist.id}|${playlist.name}|${playlist.createdAt}")
                playlist.songIds.forEach { appendLine(it) }
            }.trimEnd()
        }
        file.writeText(content)
    }
}