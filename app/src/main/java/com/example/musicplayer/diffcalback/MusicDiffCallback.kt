package com.example.musicplayer.diffcalback

import androidx.recyclerview.widget.DiffUtil
import com.example.musicplayer.MusicFile

class MusicDiffCallback : DiffUtil.ItemCallback<MusicFile>() {

    override fun areItemsTheSame(oldItem: MusicFile, newItem: MusicFile) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MusicFile, newItem: MusicFile) = oldItem == newItem

}