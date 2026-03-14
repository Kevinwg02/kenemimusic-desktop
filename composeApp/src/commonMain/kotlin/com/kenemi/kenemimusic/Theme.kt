package com.kenemi.kenemimusic

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object KenemiColors {

    // Accent principal — bleu électrique
    val Blue400    = Color(0xFF00A2FF)  // principal
    val Blue600    = Color(0xFF0077CC)  // foncé
    val Blue200    = Color(0xFF66C8FF)  // clair

    // Backgrounds dark
    val DarkBg       = Color(0xFF0D1117)
    val DarkSurface  = Color(0xFF161B22)
    val DarkElevated = Color(0xFF21262D)
    val DarkBorder   = Color(0xFF30363D)

    // Backgrounds light
    val LightBg       = Color(0xFFF4F8FC)
    val LightSurface  = Color(0xFFFFFFFF)
    val LightElevated = Color(0xFFE8F4FF)
    val LightBorder   = Color(0xFFBDD8F0)

    // Textes dark
    val DarkTextPrimary   = Color(0xFFE6EDF3)
    val DarkTextSecondary = Color(0xFF8B949E)
    val DarkTextTertiary  = Color(0xFF484F58)

    // Textes light
    val LightTextPrimary   = Color(0xFF0D1117)
    val LightTextSecondary = Color(0xFF4A5568)
    val LightTextTertiary  = Color(0xFF8B949E)

    // Sémantiques
    val Favorite = Color(0xFFE05C5C)
    val Success  = Color(0xFF4CAF82)
    val Warning  = Color(0xFFEFA627)
}

private val DarkColorScheme = darkColorScheme(
    primary          = KenemiColors.Blue400,
    onPrimary        = Color.White,
    primaryContainer = KenemiColors.Blue600,
    background       = KenemiColors.DarkBg,
    surface          = KenemiColors.DarkSurface,
    surfaceVariant   = KenemiColors.DarkElevated,
    onBackground     = KenemiColors.DarkTextPrimary,
    onSurface        = KenemiColors.DarkTextPrimary,
    onSurfaceVariant = KenemiColors.DarkTextSecondary,
    outline          = KenemiColors.DarkBorder,
    secondary        = KenemiColors.Blue200,
    onSecondary      = KenemiColors.DarkBg,
)

private val LightColorScheme = lightColorScheme(
    primary          = KenemiColors.Blue600,
    onPrimary        = Color.White,
    primaryContainer = KenemiColors.Blue200,
    background       = KenemiColors.LightBg,
    surface          = KenemiColors.LightSurface,
    surfaceVariant   = KenemiColors.LightElevated,
    onBackground     = KenemiColors.LightTextPrimary,
    onSurface        = KenemiColors.LightTextPrimary,
    onSurfaceVariant = KenemiColors.LightTextSecondary,
    outline          = KenemiColors.LightBorder,
    secondary        = KenemiColors.Blue400,
    onSecondary      = Color.White,
)

val LocalDarkTheme = staticCompositionLocalOf { true }

@Composable
fun KenemiMusicTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}