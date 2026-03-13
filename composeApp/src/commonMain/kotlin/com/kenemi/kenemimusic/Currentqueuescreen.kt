package com.kenemi.kenemimusic

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun CurrentQueueScreen(onBack: () -> Unit) {
    val playerState = LocalPlayerState.current
    val actions = LocalPlayerActions.current

    var queue by remember(playerState.queue) { mutableStateOf(playerState.queue) }
    val currentSong = playerState.currentSong
    val currentIndex = queue.indexOfFirst { it.id == currentSong?.id }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        queue = queue.toMutableList().apply { add(to.index, removeAt(from.index)) }
        playerState.queue = queue
    }

    LaunchedEffect(currentSong?.id) {
        if (currentIndex > 2) lazyListState.animateScrollToItem(currentIndex)
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // ── En-tête ──
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Previous, contentDescription = "Retour",
                    tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Lecture en cours", fontSize = 16.sp, fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface)
                Text("${queue.size} titre${if (queue.size > 1) "s" else ""}",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (playerState.isShuffle) {
                Icon(imageVector = Icons.Shuffle, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
            if (playerState.isRepeat) {
                Icon(imageVector = Icons.Repeat, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline))

        if (queue.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune chanson en cours", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = queue,
                    key = { _, song -> song.id }
                ) { index, song ->
                    val isCurrent = song.id == currentSong?.id
                    val isPlayed = index < currentIndex
                    val isDimmed = isPlayed

                    ReorderableItem(reorderableState, key = song.id) { isDragging ->
                        QueueSongRow(
                            index = index + 1,
                            song = song,
                            isPlaying = isCurrent,
                            isCurrent = isCurrent,
                            isDimmed = isDimmed && !isDragging,
                            onClick = { if (!isCurrent) actions.playAll(queue, index) },
                            dragHandle = { DragHandle(reorderableState = this) }
                        )
                    }
                }
            }
        }
    }
}

// =====================================================
// COMPOSANTS
// =====================================================

@Composable
fun QueueSongRow(
    index: Int,
    song: Song,
    isPlaying: Boolean,
    isCurrent: Boolean,
    isDimmed: Boolean = false,
    onClick: () -> Unit,
    dragHandle: @Composable () -> Unit
) {
    val alpha = if (isDimmed) 0.45f else 1f

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(enabled = !isCurrent) { onClick() }
            .background(
                if (isCurrent) MaterialTheme.colorScheme.surfaceVariant
                else androidx.compose.ui.graphics.Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        dragHandle()

        Box(modifier = Modifier.width(20.dp), contentAlignment = Alignment.Center) {
            if (isPlaying) {
                PlayingBarsIndicator()
            } else {
                Text("$index", fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.title, fontSize = 13.sp,
                fontWeight = if (isCurrent) FontWeight.W500 else FontWeight.Normal,
                color = if (isCurrent) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "${song.artist} • ${song.album}", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Text(text = formatDuration(song.duration), fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha))
    }

    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
}

@Composable
fun DragHandle(reorderableState: sh.calvin.reorderable.ReorderableCollectionItemScope) {
    Box(
        modifier = with(reorderableState) {
            Modifier.size(32.dp).draggableHandle()
        },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = Icons.DragHandle, contentDescription = "Déplacer",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp))
    }
}

@Composable
fun PlayingBarsIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "bars")
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "b1")
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse), label = "b2")
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "b3")

    Row(horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(14.dp)) {
        listOf(bar1, bar2, bar3).forEach { fraction ->
            Box(modifier = Modifier.width(3.dp).fillMaxHeight(fraction)
                .clip(CircleShape).background(MaterialTheme.colorScheme.primary))
        }
    }
}