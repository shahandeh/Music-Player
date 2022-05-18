package com.example.musicplayer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.musicplayer.MusicPlayerApp.Companion.activityIsAvailable
import com.example.musicplayer.MusicPlayerApp.Companion.currentAlbumList
import com.example.musicplayer.MusicPlayerApp.Companion.currentArtistList
import com.example.musicplayer.MusicPlayerApp.Companion.currentMusicList
import com.example.musicplayer.MusicPlayerApp.Companion.isRepeat
import com.example.musicplayer.MusicPlayerApp.Companion.isShuffle
import com.example.musicplayer.MusicPlayerApp.Companion.musicListSortedByAlbum
import com.example.musicplayer.MusicPlayerApp.Companion.musicListSortedByArtist
import com.example.musicplayer.MusicPlayerApp.Companion.musicListSortedByTitle
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayer
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayerAction
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayerState
import com.example.musicplayer.MusicPlayerApp.Companion.musicPosition
import kotlin.random.Random

object MPlayer {

    fun getAudioList(context: Context) {
        val musicTemp = ArrayList<MusicFile>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )
        val cursor = context.contentResolver.query(
            uri, projection, null, null, null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artist = cursor.getString(4)
                val id = cursor.getString(5)

                val mediaFile = MusicFile(path, title, artist, album, duration, id)
                musicTemp.add(mediaFile)
            }
            cursor.close()
        }

        musicTemp.forEach { musicListSortedByTitle.add(it) }
        musicListSortedByTitle.sortBy { it.title }

        musicTemp.forEach { musicListSortedByArtist.add(it) }
        musicListSortedByArtist.sortBy { it.artist }

        musicTemp.forEach { musicListSortedByAlbum.add(it) }
        musicListSortedByAlbum.sortBy { it.album }
    }

    fun setMusicList() {
        currentMusicList.clear()
        musicListSortedByTitle.forEach { currentMusicList.add(it) }
    }

    fun setAlbumList() {

        val temp = ArrayList<MusicFile>()

        for (i in musicListSortedByAlbum.indices) {
            when {
                temp.isEmpty() -> temp.add(musicListSortedByAlbum[i])
                musicListSortedByAlbum[i - 1].album == musicListSortedByAlbum[i].album -> temp.add(
                    musicListSortedByAlbum[i])
                else -> {
                    currentAlbumList.add(setAlbumModel(temp))
                    temp.clear()
                    temp.add(musicListSortedByAlbum[i])
                }
            }
        }
        currentAlbumList.add(setAlbumModel(temp))
        temp.clear()
    }

    private fun setAlbumModel(musicList: ArrayList<MusicFile>): AlbumModel {
        val albumMusic = musicList.clone() as ArrayList<MusicFile>
        return AlbumModel(
            albumName = musicList[0].album,
            albumArt = getMusicArt(musicList[0]),
            albumMusicLis = albumMusic
        )
    }

    fun createArtistList() {
        val artistTemp = ArrayList<MusicFile>()
        for (i in musicListSortedByArtist.indices) {
            when {
                artistTemp.isEmpty() -> artistTemp.add(musicListSortedByArtist[i])
                musicListSortedByArtist[i].artist == musicListSortedByArtist[i - 1].artist -> artistTemp.add(
                    musicListSortedByArtist[i])
                else -> {
                    createArtistModel(artistTemp)
                    artistTemp.clear()
                    artistTemp.add(musicListSortedByArtist[i])
                }
            }
        }
        artistTemp.add(musicListSortedByArtist.last())
        createArtistModel(artistTemp)
        artistTemp.clear()
    }

    private fun createArtistModel(musicList: ArrayList<MusicFile>) {
        val artTemp = ArrayList<ByteArray?>()
        for (i in musicList.indices) {
            if (i < 5) artTemp.add(getMusicArt(musicList[i]))
        }
        currentArtistList.add(
            ArtistModel(
                artistName = musicList[0].artist,
                trackCount = musicList.size,
                albumList = artTemp
            )
        )
    }

    fun setArtistMusicList(artistName: String) {
        currentMusicList =
            musicListSortedByArtist.filter { it.artist == artistName } as ArrayList<MusicFile>
    }

    fun getMusicArt(musicFile: MusicFile): ByteArray? {
        val uri = Uri.parse(musicFile.path)

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        return retriever.embeddedPicture
    }

    fun getMusicArt(): ByteArray? {
        val uri = Uri.parse(currentMusicList[musicPosition].path)

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        return retriever.embeddedPicture
    }

    fun playCurrentMusic(context: Context) {
        if (musicPlayerState) {
            musicPlayer.stop()
            musicPlayer.release()
        }
        val uri = Uri.parse(currentMusicList[musicPosition].path)
        musicPlayer = MediaPlayer.create(context, uri)
        musicPlayer.start()
    }

    private fun setPreviousPosition() {
        musicPosition = when {
            isRepeat -> {
                musicPosition
            }
            isShuffle -> {
                Random.nextInt(0, currentMusicList.size - 1)
            }
            else -> {
                if (musicPosition - 1 < 0) currentMusicList.size - 1 else musicPosition - 1
            }
        }
    }

    private fun playPreviousMusic(context: Context) {
        musicPlayer.stop()
        musicPlayer.release()
        val uri = Uri.parse(currentMusicList[musicPosition].path)
        musicPlayer = MediaPlayer.create(context, uri)
        if (musicPlayerState) musicPlayer.start()
    }

    private fun setNextPosition() {
        musicPosition = when {
            isRepeat -> {
                musicPosition
            }
            isShuffle -> {
                Random.nextInt(0, currentMusicList.size - 1)
            }
            else -> {
                (musicPosition + 1) % currentMusicList.size
            }
        }
    }

    private fun playNextMusic(context: Context) {
        musicPlayer.stop()
        musicPlayer.release()
        val uri = Uri.parse(currentMusicList[musicPosition].path)
        musicPlayer = MediaPlayer.create(context, uri)
        if (musicPlayerState) musicPlayer.start()
    }

    fun serviceActionPrevious(context: Context) {
        setPreviousPosition()
        playPreviousMusic(context)
        if (activityIsAvailable) musicPlayerAction.setMetaData()
    }

    fun serviceActionPlay() {
        if (musicPlayer.isPlaying) musicPlayer.pause()
        else musicPlayer.start()
        musicPlayerState = musicPlayer.isPlaying
        if (activityIsAvailable) musicPlayerAction.playButtonChanged()
    }

    fun serviceActionNext(context: Context) {
        setNextPosition()
        playNextMusic(context)
        if (activityIsAvailable) musicPlayerAction.setMetaData()
    }

    fun musicPlayerPlayedTimeToInt() = musicPlayer.currentPosition / 1000

    fun musicPlayerPlayedTimeToString() = timeFormatter(musicPlayer.currentPosition)

    fun musicPlayerDurationToInt() = musicPlayer.duration / 1000

    fun musicPlayerDurationToString() = timeFormatter(musicPlayer.duration)

    fun musicPlayerCurrentMusicTitle() = currentMusicList[musicPosition].title

    fun musicPlayerCurrentMusicArtist() = currentMusicList[musicPosition].artist

    fun currentMusicPositionInPositionList() = "${musicPosition + 1} of ${currentMusicList.size}"

    fun ImageView.glide(byteArray: ByteArray?) {
        if (byteArray != null) {
            Glide.with(this)
                .load(byteArray)
                .into(this)
        }
    }

    fun timeFormatter(time: Int): String {
        val temp = time / 1000
        val minute = (temp / 60).toString().padStart(2, '0')
        val second = (temp % 60).toString().padStart(2, '0')
        return "$minute:$second"
    }

}