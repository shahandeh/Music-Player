package com.example.musicplayer.listadapter

import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.MPlayer.glide
import com.example.musicplayer.MPlayer.timeFormatter
import com.example.musicplayer.MusicFile
import com.example.musicplayer.R
import com.example.musicplayer.databinding.SongSampleBinding
import com.example.musicplayer.diffcalback.MusicDiffCallback

class MusicListAdapter(private val fn: (position: Int) -> Unit) :
    ListAdapter<MusicFile, MusicListAdapter.SongViewHolder>(
        MusicDiffCallback()
    ) {

    inner class SongViewHolder(private val binding: SongSampleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(music: MusicFile) {
            binding.titleSongListRecyclerViewSample.text = music.title
            binding.artistSongListRecyclerViewSample.text = music.artist
            binding.timeSongListRecyclerViewSample.text = timeFormatter(music.duration.toInt())
            val image = getAlbumArt(music.path)
            if (image == null) binding.coverSongListRecyclerViewSample.setImageResource(R.drawable.ic_android)
            else binding.coverSongListRecyclerViewSample.glide(image)
            binding.root.setOnClickListener { fn(adapterPosition) }
        }

        private fun getAlbumArt(uri: String): ByteArray? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            retriever.release()
            return art
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SongViewHolder(
            SongSampleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}