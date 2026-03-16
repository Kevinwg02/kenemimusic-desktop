package com.kenemi.kenemimusic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SongsScreen() {
    val library = LocalMusicLibrary.current
    val actions = LocalPlayerActions.current
    val playerState = LocalPlayerState.current

    val grouped = remember(library.songs) {
        library.songs.groupBy { it.title.firstOrNull()?.uppercaseChar() ?: '#' }.toSortedMap()
    }
    val letters = grouped.keys.toList()
    val letterIndices = remember(grouped) {
        var index = 0
        grouped.map { (letter, songs) ->
            val result = letter to index
            index += songs.size
            result
        }.toMap()
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Toutes les chansons", fontSize = 14.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface)
            Text(text = "${library.songs.size} titres", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        when {
            library.isLoading -> Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            library.songs.isEmpty() -> Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Aucune chanson", fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Configurez votre dossier dans Paramètres", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
            else -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        itemsIndexed(
                            items = library.songs,
                            key = { _, song -> song.id }
                        ) { index, song ->
                            SongRow(
                                index = index + 1,
                                song = song,
                                isPlaying = song.id == playerState.currentSong?.id,
                                onClick = { actions.playAll(library.songs, index) }
                            )
                        }
                    }

                    AlphabetBar(
                        letters = letters,
                        onLetterClick = { letter ->
                            val index = letterIndices[letter] ?: return@AlphabetBar
                            scope.launch { listState.animateScrollToItem(index) }
                        }
                    )
                }
            }
        }
    }
}