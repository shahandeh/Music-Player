package com.example.musicplayer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MPlayer.createArtistList
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
import com.example.musicplayer.MPlayer.setAlbumList
import com.example.musicplayer.MPlayer.setArtistMusicList
import com.example.musicplayer.MPlayer.setMusicList
import com.example.musicplayer.MusicPlayerApp.Companion.activityIsAvailable
import com.example.musicplayer.MusicPlayerApp.Companion.applicationIsAvailable
import com.example.musicplayer.MusicPlayerApp.Companion.currentAlbumList
import com.example.musicplayer.MusicPlayerApp.Companion.currentArtistList
import com.example.musicplayer.MusicPlayerApp.Companion.currentMusicList
import com.example.musicplayer.MusicPlayerApp.Companion.isRepeat
import com.example.musicplayer.MusicPlayerApp.Companion.isShuffle
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayerAction
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayerState
import com.example.musicplayer.MusicPlayerApp.Companion.musicPosition
import com.example.musicplayer.databinding.ActivityMusicPlayerBinding
import com.example.musicplayer.listadapter.AlbumListAdapter
import com.example.musicplayer.listadapter.ArtistListAdapter
import com.example.musicplayer.listadapter.MusicListAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayer : AppCompatActivity(), MusicPlayerAction {

    private lateinit var binding: ActivityMusicPlayerBinding
    private lateinit var musicListAdapter: MusicListAdapter
    private lateinit var albumListAdapter: AlbumListAdapter
    private lateinit var artistListAdapter: ArtistListAdapter

    private lateinit var seekBarChangeListener: SeekBarChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicPlayerAction = this
        serviceAction()

        if (!applicationIsAvailable) {
            getAudioList(this)
            createArtistList()
            setAlbumList()
        }
        applicationIsAvailable = true
        allSongBtn()
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

    private fun serviceAction(intentAction: String? = null) {
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        if (intentAction != null) serviceIntent.action = intentAction
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
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

    private fun musicListAdapter() {
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
        musicPlayerState = true
        playCurrentMusic(this)
        setMetaData()
        seekBarTracker()
    }

    private fun playNext() {
        serviceAction(ACTION_NEXT)
    }

    private fun playPrevious() {
        serviceAction(ACTION_PREVIOUS)
    }

    private fun playPause() {
        serviceAction(ACTION_PLAY)
    }

    private fun shuffleBtn() {
        isShuffle = isShuffle.not()
        isRepeat = false
        binding.maxPlayerShuffle.isChecked = isShuffle
        binding.maxPlayerRepeat.isChecked = false
    }

    private fun repeatBtn() {
        isRepeat = isRepeat.not()
        isShuffle = false
        binding.maxPlayerRepeat.isChecked = isRepeat
        binding.maxPlayerShuffle.isChecked = false
    }

    override fun setMetaData() {
        val temp = getMusicArt()
        with(binding) {
            miniPlayerArtist.text = musicPlayerCurrentMusicArtist()
            miniPlayerTitle.text = musicPlayerCurrentMusicTitle()
            maxPlayerCurrentSongPosition.text = currentMusicPositionInPositionList()
            maxPlayerTitle.text = musicPlayerCurrentMusicTitle()
            maxPlayerShuffle.isChecked = isShuffle
            maxPlayerRepeat.isChecked = isRepeat
            maxPlayerArtist.text = musicPlayerCurrentMusicArtist()
            maxPlayerDuration.text = musicPlayerDurationToString()
            maxPlayerSeekBar.max = musicPlayerDurationToInt()

            if (temp != null) {
                maxPlayerAlbumArt.glide(temp)
                miniPlayerAlbumArt.glide(temp)
            }
        }
    }

    override fun playButtonChanged() {
        with(binding) {
            if (musicPlayerState) {
                miniPlayerPlay.setBackgroundResource(R.drawable.ic_play)
                maxPlayerPlay.setBackgroundResource(R.drawable.ic_play)
                seekBarTracker()
            } else {
                miniPlayerPlay.setBackgroundResource(R.drawable.ic_pause)
                maxPlayerPlay.setBackgroundResource(R.drawable.ic_pause)
            }
        }
    }

    private fun seekBarTracker() {
        lifecycleScope.launch {
            while (musicPlayerState) {
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

    override fun onStart() {
        super.onStart()
        activityIsAvailable = true
    }

    override fun onStop() {
        super.onStop()
        activityIsAvailable = false
    }
}