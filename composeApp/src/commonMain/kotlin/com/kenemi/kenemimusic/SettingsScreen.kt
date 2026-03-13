package com.kenemi.kenemimusic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// =====================================================
// ÉCRAN PARAMÈTRES (commonMain)
// =====================================================

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val library = LocalMusicLibrary.current
    val scope = rememberCoroutineScope()
    var folderPath by remember { mutableStateOf(library.musicFolderPath) }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Paramètres",
            fontSize = 22.sp,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsSectionTitle("Bibliothèque musicale")

        SettingsCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Dossier musique",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (folderPath.isBlank()) "Aucun dossier sélectionné" else folderPath,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            // Appel plateforme (défini dans jvmMain)
                            val selected = pickMusicFolder(folderPath)
                            if (selected != null) {
                                folderPath = selected
                                scope.launch {
                                    library.onScanStarted(selected)
                                    try {
                                        val songs = scanMusicFolder(selected)
                                        library.onSongsLoaded(songs)
                                        saveMusicFolder(selected)
                                    } catch (e: Exception) {
                                        library.onScanError(e.message ?: "Erreur")
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Choisir un dossier", fontSize = 13.sp)
                    }

                    if (folderPath.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    library.onScanStarted(folderPath)
                                    try {
                                        val songs = scanMusicFolder(folderPath)
                                        library.onSongsLoaded(songs)
                                    } catch (e: Exception) {
                                        library.onScanError(e.message ?: "Erreur")
                                    }
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text("Rescanner", fontSize = 13.sp)
                        }
                    }
                }

                when {
                    library.isLoading -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Scan en cours...", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    library.songs.isNotEmpty() -> Text(
                        text = "✓ ${library.songs.size} chansons • ${library.artists.size} artistes • ${library.albums.size} albums",
                        fontSize = 12.sp,
                        color = KenemiColors.Success
                    )
                    library.errorMessage != null -> Text(
                        text = "⚠ ${library.errorMessage}",
                        fontSize = 12.sp,
                        color = KenemiColors.Warning
                    )
                }
            }
        }

        SettingsSectionTitle("Apparence")

        SettingsCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Thème sombre",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isDarkTheme) "Mode nuit activé" else "Mode clair activé",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onThemeToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.W500,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.08.sp
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        content()
    }
}

// Fonctions expect — implémentées dans jvmMain
expect fun pickMusicFolder(currentPath: String): String?
expect fun scanMusicFolder(path: String): List<Song>
expect fun saveMusicFolder(path: String)

expect fun saveFavorites(ids: Set<Long>)

expect fun savePlaylists(playlists: List<Playlist>)