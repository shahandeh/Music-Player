package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MPlayer.currentMusicPositionInPositionList
import com.example.musicplayer.MPlayer.getAudioList
import com.example.musicplayer.MPlayer.getMusicArt
import com.example.musicplayer.MPlayer.glide
import com.example.musicplayer.MPlayer.musicPlayerCurrentMusicArtist
import com.example.musicplayer.MPlayer.musicPlayerCurrentMusicTitle
import com.example.musicplayer.MPlayer.musicPlayerDurationToInt
import com.example.musicplayer.MPlayer.musicPlayerDurationToString
import com.example.musicplayer.MPlayer.musicPlayerPlayedTimeToInt
import com.example.musicplayer.MPlayer.musicPlayerPlayedTimeToString
import com.example.musicplayer.MPlayer.playCurrentMusic
import com.example.musicplayer.MPlayer.playNextMusic
import com.example.musicplayer.MPlayer.playPreviousMusic
import com.example.musicplayer.MPlayer.setAlbumList
import com.example.musicplayer.MPlayer.createArtistList
import com.example.musicplayer.MPlayer.setArtistMusicList
import com.example.musicplayer.MPlayer.setMusicList
import com.example.musicplayer.MPlayer.setNextPosition
import com.example.musicplayer.MPlayer.setPreviousPosition
import com.example.musicplayer.MusicPlayerApp.Companion.currentAlbumList
import com.example.musicplayer.MusicPlayerApp.Companion.currentArtistList
import com.example.musicplayer.MusicPlayerApp.Companion.currentMusicList
import com.example.musicplayer.MusicPlayerApp.Companion.musicPosition
import com.example.musicplayer.databinding.ActivityMusicPlayerBinding
import com.example.musicplayer.listadapter.AlbumListAdapter
import com.example.musicplayer.listadapter.ArtistListAdapter
import com.example.musicplayer.listadapter.MusicListAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayerBinding
    private lateinit var musicListAdapter: MusicListAdapter
    private lateinit var albumListAdapter: AlbumListAdapter
    private lateinit var artistListAdapter: ArtistListAdapter

    private lateinit var seekBarChangeListener: SeekBarChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAudioList(this)
        allSongBtn()
        createArtistList()
        setAlbumList()
        setMetaData()
        seekBarTracker()
        seekBarListener()


        binding.maxPlayerPrevious.setOnClickListener { playPrevious() }
        binding.miniPlayerPrevious.setOnClickListener { playPrevious() }

        binding.maxPlayerPlay.setOnClickListener { playPause() }
        binding.miniPlayerPlay.setOnClickListener { playPause() }

        binding.maxPlayerNext.setOnClickListener { playNext() }
        binding.miniPlayerNext.setOnClickListener { playNext() }

        binding.maxPlayerShuffle.setOnClickListener { shuffleBtn() }

        binding.maxPlayerRepeat.setOnClickListener { repeatBtn() }

        binding.sideMenuAllSong.setOnClickListener { allSongBtn() }

        binding.sideMenuAlbum.setOnClickListener { albumSongBtn() }

        binding.sideMenuArtist.setOnClickListener { artistListBtn() }

    }

    private fun allSongBtn() {
        setMusicList()
        musicListAdapter()
    }

    private fun albumSongBtn() {
        albumListAdapter = AlbumListAdapter {
            currentMusicList = currentAlbumList[it].albumMusicLis
            musicListAdapter()
        }
        binding.musicListRecyclerView.apply {
            adapter = albumListAdapter
            layoutManager = GridLayoutManager(this@MusicPlayer, 2)
        }
        albumListAdapter.submitList(currentAlbumList)
    }

    private fun musicListAdapter(){
        musicListAdapter = MusicListAdapter {
            musicPosition = it
            playSelectedMusic()
        }
        binding.musicListRecyclerView.apply {
            adapter = musicListAdapter
            layoutManager = LinearLayoutManager(this@MusicPlayer)
        }
        musicListAdapter.submitList(currentMusicList)
    }

    private fun artistListBtn() {
        artistListAdapter = ArtistListAdapter {
            setArtistMusicList(currentArtistList[it].artistName)
            musicListAdapter()
        }
        binding.musicListRecyclerView.apply {
            adapter = artistListAdapter
            layoutManager = LinearLayoutManager(this@MusicPlayer)
        }
        artistListAdapter.submitList(currentArtistList)
    }


    private fun playSelectedMusic() {
        MusicPlayerApp.musicPlayerState = true
        playCurrentMusic(this)
        setMetaData()
        seekBarTracker()
    }

    private fun playNext() {
        setNextPosition()
        playNextMusic(this)
        setMetaData()
        seekBarTracker()
    }

    private fun playPrevious() {
        setPreviousPosition()
        playPreviousMusic(this)
        setMetaData()
        seekBarTracker()
    }

    private fun playPause() {
        if (MusicPlayerApp.musicPlayer.isPlaying) {
            MusicPlayerApp.musicPlayer.pause()
            binding.maxPlayerPlay.setImageResource(R.drawable.ic_play)

        } else {
            MusicPlayerApp.musicPlayer.start()
            binding.maxPlayerPlay.setImageResource(R.drawable.ic_pause)
            seekBarTracker()
        }
        MusicPlayerApp.musicPlayerState = MusicPlayerApp.musicPlayer.isPlaying
    }

    private fun shuffleBtn() {
        MusicPlayerApp.isShuffle = MusicPlayerApp.isShuffle.not()
        MusicPlayerApp.isRepeat = false
        binding.maxPlayerShuffle.isChecked = MusicPlayerApp.isShuffle
        binding.maxPlayerRepeat.isChecked = false
    }

    private fun repeatBtn() {
        MusicPlayerApp.isRepeat = MusicPlayerApp.isRepeat.not()
        MusicPlayerApp.isShuffle = false
        binding.maxPlayerRepeat.isChecked = MusicPlayerApp.isRepeat
        binding.maxPlayerShuffle.isChecked = false
    }

    private fun setMetaData() {
        binding.maxPlayerCurrentSongPosition.text = currentMusicPositionInPositionList()
        binding.maxPlayerDuration.text = musicPlayerDurationToString()
        binding.maxPlayerTitle.text = musicPlayerCurrentMusicTitle()
        binding.maxPlayerArtist.text = musicPlayerCurrentMusicArtist()
        binding.maxPlayerSeekBar.max = musicPlayerDurationToInt()
        binding.miniPlayerTitle.text = musicPlayerCurrentMusicTitle()
        binding.miniPlayerArtist.text = musicPlayerCurrentMusicArtist()

        val temp = getMusicArt()
        if (temp != null) {
            binding.apply {
                maxPlayerAlbumArt.glide(temp)
                miniPlayerAlbumArt.glide(temp)
            }
        }
    }

    private fun seekBarTracker() {
        lifecycleScope.launch {
            while (MusicPlayerApp.musicPlayer.isPlaying) {
                updateSeekBarPosition()
                delay(200)
            }
        }
    }

    private fun updateSeekBarPosition() {
        binding.maxPlayerSeekBar.progress = musicPlayerPlayedTimeToInt()
        binding.maxPlayerPlayedTime.text = musicPlayerPlayedTimeToString()
    }

    private fun seekBarListener() {
        seekBarChangeListener = SeekBarChangeListener()
        binding.maxPlayerSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
    }
}