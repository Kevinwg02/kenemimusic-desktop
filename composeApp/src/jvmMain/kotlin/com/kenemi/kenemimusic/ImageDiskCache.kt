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

    fun clear() {
        dir.listFiles()?.forEach { it.delete() }
    }

    fun sizeKb(): Long {
        return dir.listFiles()?.sumOf { it.length() }?.div(1024) ?: 0L
    }
}