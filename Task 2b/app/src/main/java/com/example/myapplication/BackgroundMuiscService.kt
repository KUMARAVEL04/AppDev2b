package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class BackgroundMuiscService : Service() {
    lateinit var mediaPlayer: MediaPlayer
    var length:Int = 0


    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer.setVolume(0.4f, 0.4f)
        mediaPlayer.isLooping = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            "PLAY" -> playMusic()
            "PAUSE" -> pauseMusic()
            "MUTE" -> muteMusic()
            "UNMUTE" -> unmuteMusic()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
    fun muteMusic(){
        mediaPlayer.setVolume(0f,0f)
    }
    fun unmuteMusic(){
        mediaPlayer.setVolume(0.4f, 0.4f)
    }
    fun playMusic(){
        mediaPlayer.seekTo(length)
        mediaPlayer.start()
    }
    fun pauseMusic(){
        mediaPlayer.pause()
        length=mediaPlayer.currentPosition
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}