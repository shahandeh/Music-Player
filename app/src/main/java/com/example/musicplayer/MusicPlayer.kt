package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.musicplayer.MPlayer.currentMusicPositionInPositionList
import com.example.musicplayer.MPlayer.getAudioList
import com.example.musicplayer.MPlayer.glide
import com.example.musicplayer.MPlayer.musicPlayerCurrentMusicArtist
import com.example.musicplayer.MPlayer.musicPlayerCurrentMusicTitle
import com.example.musicplayer.MPlayer.musicPlayerDurationToInt
import com.example.musicplayer.MPlayer.musicPlayerDurationToString
import com.example.musicplayer.MusicPlayerApp.Companion.currentMusicList
import com.example.musicplayer.MusicPlayerApp.Companion.musicPosition
import com.example.musicplayer.databinding.ActivityMusicPlayerBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayerBinding
    private lateinit var musicListAdapter: MusicListAdapter
    private lateinit var seekBarChangeListener: SeekBarChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAudioList(this)
        setMetaData()
        seekBarTracker()
        seekBarListener()

        musicListAdapterInit()

        binding.maxPlayerPrevious.setOnClickListener { playPrevious() }
        binding.miniPlayerPrevious.setOnClickListener { playPrevious() }

        binding.maxPlayerPlay.setOnClickListener { playPause() }
        binding.miniPlayerPlay.setOnClickListener { playPause() }

        binding.maxPlayerNext.setOnClickListener { playNext() }
        binding.miniPlayerNext.setOnClickListener { playNext() }

        binding.maxPlayerShuffle.setOnClickListener { shuffleBtn() }

        binding.maxPlayerRepeat.setOnClickListener { repeatBtn() }

    }

    private fun musicListAdapterInit() {
        musicListAdapter = MusicListAdapter {
            musicPosition = it
            playSelectedMusic()
        }
        binding.musicListRecyclerView.apply {
            adapter = musicListAdapter
            addItemDecoration(DividerItemDecoration(this@MusicPlayer, DividerItemDecoration.VERTICAL))
        }
        musicListAdapter.submitList(currentMusicList)
    }

    private fun playSelectedMusic() {
        MusicPlayerApp.musicPlayerState = true
        MPlayer.playCurrentMusic(this)
        setMetaData()
        seekBarTracker()
    }

    private fun playNext() {
        MPlayer.setNextPosition()
        MPlayer.playNextMusic(this)
        setMetaData()
        seekBarTracker()
    }

    private fun playPrevious() {
        MPlayer.setPreviousPosition()
        MPlayer.playPreviousMusic(this)
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

        val temp = MPlayer.getMusicArt()
        if (temp != null) {
            binding.apply {
                maxPlayerAlbumArt.glide(temp)
                miniPlayerAlbumArt.glide(temp)
            }
        }
//        updateNotification()
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
        binding.maxPlayerSeekBar.progress = MPlayer.musicPlayerPlayedTimeToInt()
        binding.maxPlayerPlayedTime.text = MPlayer.musicPlayerPlayedTimeToString()
    }

    private fun seekBarListener() {
        seekBarChangeListener = SeekBarChangeListener()
        binding.maxPlayerSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
    }
}