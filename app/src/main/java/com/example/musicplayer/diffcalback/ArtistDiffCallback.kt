package com.example.musicplayer.diffcalback

import androidx.recyclerview.widget.DiffUtil
import com.example.musicplayer.ArtistModel

class ArtistDiffCallback: DiffUtil.ItemCallback<ArtistModel>() {

    override fun areItemsTheSame(oldItem: ArtistModel, newItem: ArtistModel) = oldItem.artistName == newItem.artistName

    override fun areContentsTheSame(oldItem: ArtistModel, newItem: ArtistModel) = oldItem == newItem

}