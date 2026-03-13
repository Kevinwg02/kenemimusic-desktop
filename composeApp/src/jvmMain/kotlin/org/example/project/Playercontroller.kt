package com.kenemi.kenemimusic

import kotlinx.coroutines.*

class PlayerController(
    private val player: MusicPlayer,
    private val state: PlayerStateHolder
) : PlayerActions {

    // Scope sur le thread principal (Swing EDT pour desktop)
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        player.onTrackFinished = {
            mainScope.launch { next() }
        }
        player.onPositionChanged = { pos ->
            mainScope.launch { state.progress = pos }
        }
        player.onTimeChanged = { ms ->
            mainScope.launch { state.currentMs = ms }
        }
    }

    override fun play(song: Song) {
        state.currentSong = song
        state.isPlaying = true
        state.progress = 0f
        state.currentMs = 0L
        player.play(song.filePath)
    }

    override fun playAll(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return
        state.queue = songs
        play(songs[startIndex.coerceIn(0, songs.size - 1)])
    }

    override fun togglePlayPause() {
        if (state.isPlaying) {
            player.pause()
            state.isPlaying = false
        } else {
            player.resume()
            state.isPlaying = true
        }
    }

    override fun next() {
        val queue = state.queue
        if (queue.isEmpty()) return
        val currentIndex = queue.indexOfFirst { it.id == state.currentSong?.id }
        val nextIndex = when {
            state.isRepeat  -> currentIndex
            state.isShuffle -> queue.indices.random()
            currentIndex < queue.size - 1 -> currentIndex + 1
            else -> 0
        }
        play(queue[nextIndex])
    }

    override fun previous() {
        val queue = state.queue
        if (queue.isEmpty()) return
        if (state.currentMs > 3000L) {
            seekTo(0f)
            return
        }
        val currentIndex = queue.indexOfFirst { it.id == state.currentSong?.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else queue.size - 1
        play(queue[prevIndex])
    }

    override fun seekTo(position: Float) {
        player.seekTo(position)
        state.progress = position
    }

    override fun toggleShuffle() { state.isShuffle = !state.isShuffle }
    override fun toggleRepeat()  { state.isRepeat  = !state.isRepeat  }

    fun release() {
        mainScope.cancel()
        player.release()
    }
}