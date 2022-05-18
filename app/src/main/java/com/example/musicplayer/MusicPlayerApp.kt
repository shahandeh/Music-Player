package com.example.musicplayer

import android.app.Application
import android.media.MediaPlayer

class MusicPlayerApp: Application() {

    companion object {
        var musicPlayer = MediaPlayer()

        var musicListSortedByTitle = ArrayList<MusicFile>()
        var musicListSortedByArtist = ArrayList<MusicFile>()
        var musicListSortedByAlbum = ArrayList<MusicFile>()

        var currentArtistList = ArrayList<ArtistModel>()
        var currentAlbumList = ArrayList<AlbumModel>()
        var currentMusicList = ArrayList<MusicFile>()

        var musicPlayerState = musicPlayer.isPlaying
        var musicPosition = 0
        var isShuffle = false
        var isRepeat = false

    }

}