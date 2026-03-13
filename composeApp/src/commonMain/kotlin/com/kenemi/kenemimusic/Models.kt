package com.kenemi.kenemimusic

// =====================================================
// TOUS LES MODÈLES DE DONNÉES
// =====================================================

data class Song(
    val id: Long = 0L,
    val title: String = "Titre inconnu",
    val artist: String = "Artiste inconnu",
    val album: String = "Album inconnu",
    val duration: Long = 0L,
    val filePath: String = "",
    val albumArtUrl: String? = null
)

data class Artist(
    val id: Long,
    val name: String,
    val songCount: Int,
    val imageUrl: String? = null
)

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val year: Int = 0,
    val songCount: Int = 0,
    val coverUrl: String? = null
)