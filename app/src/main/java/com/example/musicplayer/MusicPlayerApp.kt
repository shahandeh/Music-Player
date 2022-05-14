package com.example.musicplayer

import android.app.Application
import android.media.MediaPlayer

class MusicPlayerApp: Application() {

    companion object {
        var musicPlayer = MediaPlayer()
        var currentMusicList = ArrayList<MusicFile>()
        var musicPlayerState = musicPlayer.isPlaying
        var musicPosition = 0
        var isShuffle = false
        var isRepeat = false

    }

}