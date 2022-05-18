//package com.example.musicplayer.listadapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.musicplayer.AlbumModel
//import com.example.musicplayer.MPlayer.getMusicArt
//import com.example.musicplayer.MPlayer.glide
//import com.example.musicplayer.databinding.ArtistAlbumRecyclerViewSampleBinding
//import com.example.musicplayer.diffcalback.AlbumDiffCallback
//
//class ArtistAlbumAdapter(private val fn: (position: Int) -> Unit):
//ListAdapter<AlbumModel, ArtistAlbumAdapter.ArtistAlbumViewHolder>(AlbumDiffCallback()){
//
//    inner class ArtistAlbumViewHolder(private val binding: ArtistAlbumRecyclerViewSampleBinding):
//            RecyclerView.ViewHolder(binding.root){
//                fun bind(albumModel: AlbumModel){
//                    val musicArt = getMusicArt(albumModel.musicFileList[0])
//                    with(binding){
//                        if (musicArt != null) albumImageAlbumRecyclerView.glide(musicArt)
//                        albumNameAlbumRecyclerView.text = albumModel.albumName
//                    }
//                }
//            }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
//        ArtistAlbumViewHolder(
//            ArtistAlbumRecyclerViewSampleBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//
//    override fun onBindViewHolder(holder: ArtistAlbumViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//}