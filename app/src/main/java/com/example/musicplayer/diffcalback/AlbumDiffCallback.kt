package com.example.musicplayer.diffcalback

import androidx.recyclerview.widget.DiffUtil
import com.example.musicplayer.AlbumModel
import com.example.musicplayer.MusicFile

class AlbumDiffCallback : DiffUtil.ItemCallback<AlbumModel>() {

    override fun areItemsTheSame(oldItem: AlbumModel, newItem: AlbumModel) =
        oldItem.albumName == newItem.albumName

    override fun areContentsTheSame(oldItem: AlbumModel, newItem: AlbumModel) =
        oldItem == newItem
}