package com.kenemi.kenemimusic

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image as SkiaImage

// =====================================================
// CACHE GLOBAL D'IMAGES
// =====================================================

private val imageCache = mutableMapOf<String, ImageBitmap?>()

// =====================================================
// COMPOSANT ASYNC IMAGE
// =====================================================

@Composable
fun AsyncImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {}
) {
    if (url.isNullOrBlank()) {
        placeholder()
        return
    }

    var bitmap by remember(url) { mutableStateOf(imageCache[url]) }
    var isLoading by remember(url) { mutableStateOf(bitmap == null) }

    LaunchedEffect(url) {
        if (imageCache.containsKey(url)) {
            bitmap = imageCache[url]
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val bytes = HttpClient().use { client ->
                client.get(url).readBytes()
            }
            val loaded = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
            imageCache[url] = loaded
            bitmap = loaded
        } catch (e: Exception) {
            imageCache[url] = null
        }
        isLoading = false
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            bitmap != null -> Image(
                painter = BitmapPainter(bitmap!!),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
            else -> placeholder()
        }
    }
}