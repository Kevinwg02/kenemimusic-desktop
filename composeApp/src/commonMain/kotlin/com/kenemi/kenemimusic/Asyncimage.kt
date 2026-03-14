package com.kenemi.kenemimusic

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jetbrains.skia.Image as SkiaImage
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.FilterTileMode
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface

// Caches séparés : bytes bruts, images normales, images floutées
private val bytesCache = mutableMapOf<String, ByteArray>()
private val imageCache = mutableMapOf<String, ImageBitmap?>()
private val blurCache  = mutableMapOf<String, ImageBitmap?>()

// =====================================================
// IMAGE NORMALE
// =====================================================

@Composable
fun AsyncImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {}
) {
    if (url.isNullOrBlank()) { placeholder(); return }

    var bitmap by remember(url) { mutableStateOf(imageCache[url]) }

    LaunchedEffect(url) {
        if (imageCache.containsKey(url)) { bitmap = imageCache[url]; return@LaunchedEffect }
        try {
            val bytes = fetchBytes(url)
            val loaded = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
            imageCache[url] = loaded
            bitmap = loaded
        } catch (e: Exception) { imageCache[url] = null }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (bitmap != null) {
            Image(painter = BitmapPainter(bitmap!!), contentDescription = null,
                modifier = Modifier.fillMaxSize(), contentScale = contentScale)
        } else {
            placeholder()
        }
    }
}

// =====================================================
// IMAGE FLOUTÉE
// =====================================================

@Composable
fun BlurredAsyncImage(
    url: String?,
    modifier: Modifier = Modifier,
    blurRadius: Float = 40f,
) {
    if (url.isNullOrBlank()) return

    val cacheKey = "$url@$blurRadius"
    var bitmap by remember(cacheKey) { mutableStateOf(blurCache[cacheKey]) }

    LaunchedEffect(cacheKey) {
        if (blurCache.containsKey(cacheKey)) { bitmap = blurCache[cacheKey]; return@LaunchedEffect }
        try {
            val bytes = fetchBytes(url)
            val blurred = blurSkiaImage(bytes, blurRadius)
            blurCache[cacheKey] = blurred
            bitmap = blurred
        } catch (e: Exception) { blurCache[cacheKey] = null }
    }

    if (bitmap != null) {
        Image(
            painter = BitmapPainter(bitmap!!),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

// =====================================================
// UTILITAIRES
// =====================================================

private suspend fun fetchBytes(url: String): ByteArray {
    bytesCache[url]?.let { return it }
    val bytes = ImageService.client.get(url).readBytes()
    bytesCache[url] = bytes
    return bytes
}

private fun blurSkiaImage(bytes: ByteArray, radius: Float): ImageBitmap {
    val source = SkiaImage.makeFromEncoded(bytes)
    val w = source.width
    val h = source.height
    val surface = Surface.makeRasterN32Premul(w, h)
    val paint = Paint().apply {
        imageFilter = ImageFilter.makeBlur(radius, radius, FilterTileMode.CLAMP)
    }
    surface.canvas.drawImage(source, 0f, 0f, paint)
    return surface.makeImageSnapshot().toComposeImageBitmap()
}

// =====================================================
// NETTOYAGE DU CACHE
// =====================================================

fun clearAsyncImageCache() {
    bytesCache.clear()
    imageCache.clear()
    blurCache.clear()
}

fun getImageCacheSize(): Int = bytesCache.size