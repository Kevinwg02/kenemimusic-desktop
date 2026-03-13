package com.kenemi.kenemimusic

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =====================================================
// COULEURS CENTRALISÉES — modifier ici pour tout changer
// =====================================================

object KenemiColors {

    // Accent principal (violet)
    val Purple400    = Color(0xFF7F77DD)
    val Purple600    = Color(0xFF534AB7)
    val Purple200    = Color(0xFFAFA9EC)

    // Backgrounds dark
    val DarkBg       = Color(0xFF121212)  // fond principal
    val DarkSurface  = Color(0xFF1E1E1E)  // cartes, sidebar
    val DarkElevated = Color(0xFF2A2A2A)  // éléments surélevés
    val DarkBorder   = Color(0xFF333333)  // bordures

    // Backgrounds light
    val LightBg       = Color(0xFFF5F5F5)
    val LightSurface  = Color(0xFFFFFFFF)
    val LightElevated = Color(0xFFEEEEEE)
    val LightBorder   = Color(0xFFDDDDDD)

    // Textes dark
    val DarkTextPrimary   = Color(0xFFFFFFFF)
    val DarkTextSecondary = Color(0xFFAAAAAA)
    val DarkTextTertiary  = Color(0xFF666666)

    // Textes light
    val LightTextPrimary   = Color(0xFF111111)
    val LightTextSecondary = Color(0xFF555555)
    val LightTextTertiary  = Color(0xFF999999)

    // Sémantiques
    val Favorite  = Color(0xFFE05C5C)  // rouge pour les favoris
    val Success   = Color(0xFF4CAF82)  // vert
    val Warning   = Color(0xFFEFA627)  // orange
}

// =====================================================
// COULEURS DU SCHÉMA MATERIAL — mappage vers KenemiColors
// =====================================================

private val DarkColorScheme = darkColorScheme(
    primary          = KenemiColors.Purple400,
    onPrimary        = Color.White,
    primaryContainer = KenemiColors.Purple600,
    background       = KenemiColors.DarkBg,
    surface          = KenemiColors.DarkSurface,
    surfaceVariant   = KenemiColors.DarkElevated,
    onBackground     = KenemiColors.DarkTextPrimary,
    onSurface        = KenemiColors.DarkTextPrimary,
    onSurfaceVariant = KenemiColors.DarkTextSecondary,
    outline          = KenemiColors.DarkBorder,
    secondary        = KenemiColors.Purple200,
    onSecondary      = KenemiColors.DarkBg,
)

private val LightColorScheme = lightColorScheme(
    primary          = KenemiColors.Purple600,
    onPrimary        = Color.White,
    primaryContainer = KenemiColors.Purple200,
    background       = KenemiColors.LightBg,
    surface          = KenemiColors.LightSurface,
    surfaceVariant   = KenemiColors.LightElevated,
    onBackground     = KenemiColors.LightTextPrimary,
    onSurface        = KenemiColors.LightTextPrimary,
    onSurfaceVariant = KenemiColors.LightTextSecondary,
    outline          = KenemiColors.LightBorder,
    secondary        = KenemiColors.Purple400,
    onSecondary      = Color.White,
)

// =====================================================
// COMPOSITION LOCAL pour accéder au thème partout
// =====================================================

val LocalDarkTheme = staticCompositionLocalOf { true }

// =====================================================
// COMPOSABLE PRINCIPAL DU THÈME
// =====================================================

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