package com.example.musicplayer

data class ArtistModel(
    var artistName: String,
    var trackCount: Int,
    var albumList: ArrayList<ByteArray?>
)
