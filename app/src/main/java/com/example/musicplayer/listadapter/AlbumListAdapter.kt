package com.example.musicplayer.listadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.AlbumModel
import com.example.musicplayer.MPlayer.glide
import com.example.musicplayer.databinding.AlbumSampleBinding
import com.example.musicplayer.diffcalback.AlbumDiffCallback

class AlbumListAdapter(private val fn: (position: Int) -> Unit) :
    ListAdapter<AlbumModel, AlbumListAdapter.AlbumViewHolder>(
        AlbumDiffCallback()
    ) {

    inner class AlbumViewHolder(private val binding: AlbumSampleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(albumModel: AlbumModel) {
            binding.albumNameAlbumRecyclerView.text = albumModel.albumName
            binding.albumImageAlbumRecyclerView.glide(albumModel.albumArt)
            binding.root.setOnClickListener { fn(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            AlbumSampleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}