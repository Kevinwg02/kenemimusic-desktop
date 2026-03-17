package com.kenemi.kenemimusic

import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

// =====================================================
// PLAYER VLCJ (jvmMain uniquement)
// =====================================================

class MusicPlayer {

    private var isVlcAvailable = false
    private var audioPlayer: AudioPlayerComponent? = null
    private val player: MediaPlayer? get() = audioPlayer?.mediaPlayer()

    // Callbacks vers l'UI
    var onTrackFinished: () -> Unit = {}
    var onPositionChanged: (Float) -> Unit = {}
    var onTimeChanged: (Long) -> Unit = {}

    init {
        isVlcAvailable = NativeDiscovery().discover()
        if (isVlcAvailable) {
            try {
                audioPlayer = AudioPlayerComponent()
                player?.events()?.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
                    override fun finished(mediaPlayer: MediaPlayer) {
                        onTrackFinished()
                    }
                    override fun positionChanged(mediaPlayer: MediaPlayer, newPosition: Float) {
                        onPositionChanged(newPosition)
                    }
                    override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) {
                        onTimeChanged(newTime)
                    }
                })
            } catch (e: Exception) {
                isVlcAvailable = false
            }
        }
    }

    fun play(filePath: String) {
        if (!isVlcAvailable) return
        player?.media()?.play(filePath)
    }

    fun pause() {
        if (!isVlcAvailable) return
        player?.controls()?.pause()
    }

    fun resume() {
        if (!isVlcAvailable) return
        player?.controls()?.play()
    }

    fun stop() {
        if (!isVlcAvailable) return
        player?.controls()?.stop()
    }

    fun seekTo(position: Float) {
        if (!isVlcAvailable) return
        player?.controls()?.setPosition(position)
    }

    fun isPlaying(): Boolean {
        return player?.status()?.isPlaying ?: false
    }

    fun getDuration(): Long {
        return player?.status()?.length() ?: 0L
    }

    fun release() {
        audioPlayer?.release()
    }

    fun isAvailable() = isVlcAvailable
}