package com.example.musicplayer.listadapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.ArtistModel
import com.example.musicplayer.MPlayer.glide
import com.example.musicplayer.TAG
import com.example.musicplayer.databinding.ArtistSampleBinding
import com.example.musicplayer.diffcalback.ArtistDiffCallback

//class ArtistListAdapter(): ListAdapter<ArtistModel, ArtistListAdapter.ArtistViewHolder>(ArtistDiffCallback()){
//
//    inner class ArtistViewHolder(private val binding: ArtistListBinding): RecyclerView.ViewHolder(binding.root){
//        fun bind(artistModel: ArtistModel){
//            Log.d(TAG, "bind: ")
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
//        return ArtistViewHolder(ArtistListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//    }
//
//    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
//        holder.bind(getItem(position))
//        Log.d(TAG, "bind: ")
//    }
//
//}

class ArtistListAdapter(private val fn: (position: Int) -> Unit): ListAdapter<ArtistModel, ArtistListAdapter.ArtistViewHolder>(
    ArtistDiffCallback()
) {

    inner class ArtistViewHolder(private val binding: ArtistSampleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(artistModel: ArtistModel){
            Log.d(TAG, "bind: ")
            binding.artistRecyclerViewName.text = artistModel.artistName
            binding.artistRecyclerViewTrackCount.text = artistModel.trackCount.toString()
            binding.artistRecyclerViewImageOne.setImage(artistModel, 0)
            binding.artistRecyclerViewImageTwo.setImage(artistModel, 1)
            binding.artistRecyclerViewImageThree.setImage(artistModel, 2)
            binding.artistRecyclerViewImageFour.setImage(artistModel, 3)
            binding.artistRecyclerViewImageFive.setImage(artistModel, 4)
            binding.root.setOnClickListener { fn(adapterPosition) }
        }

        private fun ImageView.setImage(artistModel: ArtistModel, index: Int){
            val artByteArray = artistModel.albumList[index]
            if (artistModel.albumList.size > index && artByteArray != null)
                glide(artByteArray)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        return ArtistViewHolder(ArtistSampleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}