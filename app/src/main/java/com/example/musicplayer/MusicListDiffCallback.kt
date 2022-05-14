package com.example.musicplayer

import androidx.recyclerview.widget.DiffUtil

class MusicListDiffCallback: DiffUtil.ItemCallback<MusicFile>() {

    override fun areItemsTheSame(oldItem: MusicFile, newItem: MusicFile) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MusicFile, newItem: MusicFile) = oldItem == newItem

}