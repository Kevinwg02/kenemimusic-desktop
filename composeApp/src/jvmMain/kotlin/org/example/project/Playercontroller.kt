package com.kenemi.kenemimusic

import kotlinx.coroutines.*

class PlayerController(
    private val player: MusicPlayer,
    private val state: PlayerStateHolder
) : PlayerActions {

    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Timestamp de début de lecture pour calculer la durée écoutée
    private var playStartMs: Long = 0L

    init {
        player.onTrackFinished = {
            mainScope.launch {
                recordCurrentPlay()
                next()
            }
        }
        player.onPositionChanged = { pos -> mainScope.launch { state.progress = pos } }
        player.onTimeChanged    = { ms  -> mainScope.launch { state.currentMs = ms  } }
    }

    override fun play(song: Song) {
        // Enregistrer la chanson précédente avant de changer
        if (state.isPlaying && state.currentSong != null) {
            recordCurrentPlay()
        }
        state.currentSong = song
        state.isPlaying = true
        state.progress = 0f
        state.currentMs = 0L
        playStartMs = System.currentTimeMillis()
        player.play(song.filePath)
    }

    override fun playAll(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return
        state.queue = songs
        play(songs[startIndex.coerceIn(0, songs.size - 1)])
    }

    override fun togglePlayPause() {
        if (state.isPlaying) {
            recordCurrentPlay()
            player.pause()
            state.isPlaying = false
        } else {
            playStartMs = System.currentTimeMillis()
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
        if (state.currentMs > 3000L) { seekTo(0f); return }
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

    private fun recordCurrentPlay() {
        val song = state.currentSong ?: return
        val elapsed = System.currentTimeMillis() - playStartMs
        // Enregistrer seulement si écouté au moins 10 secondes
        if (elapsed >= 10_000L) {
            ListeningStats.recordPlay(song.id, elapsed)
        }
        playStartMs = 0L
    }

    fun release() {
        recordCurrentPlay()
        mainScope.cancel()
        player.release()
    }
}