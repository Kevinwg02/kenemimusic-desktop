package com.kenemi.kenemimusic

import java.io.File
import java.util.Properties

// =====================================================
// GESTIONNAIRE DE PARAMÈTRES (Desktop - fichier .properties)
// =====================================================

object SettingsManager {

    private val settingsFile: File by lazy {
        val appDir = File(System.getProperty("user.home"), ".kenemimusic")
        appDir.mkdirs()
        File(appDir, "settings.properties")
    }

    private val properties: Properties by lazy {
        Properties().apply {
            if (settingsFile.exists()) {
                settingsFile.inputStream().use { load(it) }
            }
        }
    }

    // ===== DOSSIER MUSIQUE =====

    var musicFolder: String
        get() = properties.getProperty("music_folder", getDefaultMusicFolder())
        set(value) {
            properties.setProperty("music_folder", value)
            save()
        }

    fun getDefaultMusicFolder(): String {
        val home = System.getProperty("user.home")
        return when {
            // Linux
            File(home, "Musique").exists() -> File(home, "Musique").absolutePath
            File(home, "Music").exists()   -> File(home, "Music").absolutePath
            // Windows
            File(home, "Music").exists()   -> File(home, "Music").absolutePath
            else                           -> home
        }
    }

    // ===== THÈME =====

    var isDarkTheme: Boolean
        get() = properties.getProperty("dark_theme", "true").toBoolean()
        set(value) {
            properties.setProperty("dark_theme", value.toString())
            save()
        }

    // ===== SAUVEGARDE =====

    private fun save() {
        settingsFile.outputStream().use {
            properties.store(it, "Kenemi Music Settings")
        }
    }
}