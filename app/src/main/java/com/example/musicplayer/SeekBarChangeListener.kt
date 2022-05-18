package com.example.musicplayer

import android.widget.SeekBar
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayer

class SeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p2) {
            musicPlayer.seekTo(p1 * 1000)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
//        TODO("Not yet implemented")
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
//        TODO("Not yet implemented")
    }
}