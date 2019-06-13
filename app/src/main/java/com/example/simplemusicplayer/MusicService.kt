package com.example.simplemusicplayer

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log

class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    lateinit var songsList: List<RecordData>
    private lateinit var mediaPlayer: MediaPlayer
    lateinit var musicController: MusicController
    private var currentPosition: Int = 0
    private val musicBinder = MusicBinder()

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer().apply {
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            @Suppress("DEPRECATION")
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setOnPreparedListener(this@MusicService)
            setOnErrorListener(this@MusicService)
            setOnCompletionListener(this@MusicService)
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp!!.start()
        musicController.show(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        stopForeground(true)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mp!!.reset()
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (mediaPlayer.currentPosition > 0) {
            mp!!.reset()
            setNextSong()
        }
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService

    }

    override fun onBind(intent: Intent?): IBinder? {
        return musicBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer.stop()
        mediaPlayer.release()
        return false
    }

    fun prepareSong() {
        mediaPlayer.reset()

        val songToPlayId = songsList[currentPosition].id
        val trackUri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songToPlayId
        )

        try {
            mediaPlayer.setDataSource(applicationContext, trackUri)
        } catch (e: Exception) {
            Log.wtf("Prepare song", "Error during prepare song.")
        }

        mediaPlayer.prepare()
    }

    fun setPreviousSong() {
        currentPosition--
        if (currentPosition < 0) currentPosition = songsList.size - 1
        prepareSong()
    }

    fun setNextSong() {
        currentPosition++
        if (currentPosition >= songsList.size) currentPosition = 0
        prepareSong()
    }

    fun setSong(index: Int) {
        currentPosition = index
    }

    fun getPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun seek(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun pauseMusic() {
        mediaPlayer.pause()
    }

    fun startMusic() {
        mediaPlayer.start()
    }
}