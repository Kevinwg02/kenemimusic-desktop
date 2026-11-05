package com.kenemi.kenemimusic


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape


// =====================================================
// CHOISIR LA PHOTO D'UN ARTISTE
// =====================================================

@Composable
fun ArtistImagePickerDialog(
    artistName: String,
    onPicked: (String) -> Unit,   // nouvelle URL (ou "" si reset auto -> recharger)
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf(artistName) }
    var customUrl by remember { mutableStateOf("") }
    var candidates by remember { mutableStateOf<List<ArtistCandidate>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun search() {
        scope.launch {
            loading = true
            candidates = ImageService.searchArtistCandidates(query)
            loading = false
        }
    }

    LaunchedEffect(Unit) { search() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Photo de $artistName") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it },
                        label = { Text("Rechercher un autre nom") },
                        singleLine = true, modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { search() }) { Text("OK") }
                }

                Box(modifier = Modifier.fillMaxWidth().height(230.dp),
                    contentAlignment = Alignment.Center) {
                    when {
                        loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        candidates.isEmpty() -> Text("Aucun résultat", fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        else -> LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(candidates, key = { it.id }) { c ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable {
                                        ImageService.setArtistImage(artistName, c.imageUrl)
                                        onPicked(c.imageUrl)
                                    }.padding(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(64.dp).clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)) {
                                        AsyncImage(url = c.thumbUrl,
                                            modifier = Modifier.fillMaxSize(),
                                            placeholder = { ArtistInitials(name = c.name) })
                                    }
                                    Text(c.name, fontSize = 11.sp, maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = customUrl, onValueChange = { customUrl = it },
                    label = { Text("Ou coller l'URL d'une image") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = customUrl.startsWith("http"),
                onClick = {
                    ImageService.setArtistImage(artistName, customUrl.trim())
                    onPicked(customUrl.trim())
                }
            ) { Text("Utiliser l'URL") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = {
                    ImageService.resetArtistImage(artistName)
                    onPicked("")
                }) { Text("Auto") }
                TextButton(onClick = onDismiss) { Text("Annuler") }
            }
        }
    )
}

// =====================================================
// OPTIONS DE L'IMAGE D'UN ALBUM
// =====================================================

@Composable
fun AlbumImageOptionsDialog(
    album: Album,
    onChanged: () -> Unit,       // recharger l'image après changement
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Image de l'album") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        ImageService.setAlbumUsesArtistImage(album.name, album.artist)
                        onChanged(); onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
                ) { Text("Utiliser l'image de l'artiste", fontSize = 13.sp) }

                OutlinedButton(
                    onClick = {
                        ImageService.resetAlbumCover(album.name, album.artist)
                        onChanged(); onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Pochette automatique", fontSize = 13.sp) }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fermer") } }
    )
}