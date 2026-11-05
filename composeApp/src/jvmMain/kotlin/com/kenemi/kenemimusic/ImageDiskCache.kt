package com.kenemi.kenemimusic

import java.io.File
import java.security.MessageDigest

// =====================================================
// CACHE IMAGES SUR DISQUE (jvmMain)
// =====================================================

object ImageDiskCache {

    private val dir: File by lazy {
        File(System.getProperty("user.home"), ".kenemimusic/image_cache").also { it.mkdirs() }
    }

    private fun keyFor(url: String): String {
        val md5 = MessageDigest.getInstance("MD5")
            .digest(url.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return md5
    }

    fun get(url: String): ByteArray? {
        val file = File(dir, keyFor(url))
        return if (file.exists()) file.readBytes() else null
    }

    fun put(url: String, bytes: ByteArray) {
        File(dir, keyFor(url)).writeBytes(bytes)
    }

    // Cache des URLs Deezer (artistes/albums) — fichier texte séparé
    private val urlCacheFile: File by lazy {
        File(System.getProperty("user.home"), ".kenemimusic/image_urls.txt")
    }
    private val urlCache: MutableMap<String, String> by lazy {
        val map = mutableMapOf<String, String>()
        if (urlCacheFile.exists()) {
            urlCacheFile.readLines().forEach { line ->
                val parts = line.split("|", limit = 2)
                if (parts.size == 2) map[parts[0]] = parts[1]
            }
        }
        map
    }

    fun putUrl(key: String, url: String) {
        urlCache[key] = url
        urlCacheFile.writeText(urlCache.entries.joinToString("\n") { "${it.key}|${it.value}" })
    }

    fun getUrl(key: String): String? = urlCache[key]

    fun clear() {
        dir.listFiles()?.forEach { it.delete() }
        urlCache.clear()
        if (urlCacheFile.exists()) urlCacheFile.delete()
    }

    fun sizeKb(): Long {
        return dir.listFiles()?.sumOf { it.length() }?.div(1024) ?: 0L
    }
}