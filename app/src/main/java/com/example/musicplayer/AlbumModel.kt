package com.example.musicplayer

data class AlbumModel(
    var albumName: String,
    var albumArt: ByteArray? = null,
    var albumMusicLis: ArrayList<MusicFile>
)