package com.example.simplemusicplayer

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.MediaController

class MusicController(context: Context): MediaController.MediaPlayerControl, MediaController(context){
    lateinit var musicService: MusicService
    private lateinit var songsList: List<RecordData>
    private var isServiceBound: Boolean = false

    init{
        setPrevNextListeners({ musicService.setNextSong() },
            { musicService.setPreviousSong() })
        setAnchorView((context as MainActivity).findViewById(R.id.main_layout))
        setMediaPlayer(this)
        isEnabled = true
    }

    constructor(context: Context, songs: List<RecordData>) : this(context) {
        this.songsList = songs
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            connectService((service as MusicService.MusicBinder).getService(), songsList )
            musicService.musicController = this@MusicController
        }

        override fun onServiceDisconnected(name: ComponentName) {
            disconnectService()
        }
    }

    fun disconnectService() {
        isServiceBound = false
    }

    fun connectService(musicService: MusicService, songs: List<RecordData>) {
        this.musicService = musicService
        musicService.songsList = songs
        isServiceBound = true
    }

    fun songChosen(index: Int) {
        musicService.setSong(index)
        musicService.prepareSong()
        show()
    }

    override fun isPlaying(): Boolean {
        return if (isServiceBound) musicService.isPlaying()
        else { false }
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getDuration(): Int {
        return musicService.getDuration()
    }

    override fun pause() {
        musicService.pauseMusic()
    }

    override fun hide() {
    }

    override fun getBufferPercentage(): Int {
        return musicService.getDuration()
    }

    override fun seekTo(pos: Int) {
        musicService.seek(pos)
    }

    override fun getCurrentPosition(): Int {
        return musicService.getPosition()
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun start() {
        musicService.startMusic()
    }

    override fun getAudioSessionId(): Int {
        return MEDIA_PLAYER_SESSION_KEY
    }

    override fun canPause(): Boolean {
        return true
    }

    companion object {
        private const val MEDIA_PLAYER_SESSION_KEY = 451
    }

}