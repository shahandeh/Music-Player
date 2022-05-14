package com.example.musicplayer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.musicplayer.MusicPlayerApp.Companion.currentMusicList
import com.example.musicplayer.MusicPlayerApp.Companion.isRepeat
import com.example.musicplayer.MusicPlayerApp.Companion.isShuffle
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayer
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayerState
import com.example.musicplayer.MusicPlayerApp.Companion.musicPosition
import kotlin.random.Random

object MPlayer {

    fun getAudioList(context: Context){
        val audioList = arrayListOf<MusicFile>()
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
                audioList.add(mediaFile)
            }
            cursor.close()
        }
        currentMusicList = audioList
    }

    fun getMusicArt(): ByteArray?{
        val uri = Uri.parse(currentMusicList[musicPosition].path)

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        return retriever.embeddedPicture
    }

    fun playCurrentMusic(context: Context){
        if (musicPlayerState){
            musicPlayer.stop()
            musicPlayer.release()
        }
        val uri = Uri.parse(currentMusicList[musicPosition].path)
        musicPlayer = MediaPlayer.create(context, uri)
        musicPlayer.start()
    }

    fun setPreviousPosition(){
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

    fun playPreviousMusic(context: Context){
        musicPlayer.stop()
        musicPlayer.release()
        val uri = Uri.parse(currentMusicList[musicPosition].path)
        musicPlayer = MediaPlayer.create(context, uri)
        if (musicPlayerState) musicPlayer.start()
    }

    fun setNextPosition() {
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

    fun playNextMusic(context: Context) {
        musicPlayer.stop()
        musicPlayer.release()
        val uri = Uri.parse(currentMusicList[musicPosition].path)
        musicPlayer = MediaPlayer.create(context, uri)
        if (musicPlayerState) musicPlayer.start()
    }

    fun musicPlayerPlayedTimeToInt() = musicPlayer.currentPosition / 1000

    fun musicPlayerPlayedTimeToString() = timeFormatter(musicPlayer.currentPosition)

    fun musicPlayerDurationToInt() = musicPlayer.duration / 1000

    fun musicPlayerDurationToString() = timeFormatter(musicPlayer.duration)

    fun musicPlayerCurrentMusicTitle() = currentMusicList[musicPosition].title

    fun musicPlayerCurrentMusicArtist() = currentMusicList[musicPosition].artist

    fun currentMusicPositionInPositionList() = "${musicPosition + 1} of ${currentMusicList.size}"

    fun ImageView.glide(byteArray: ByteArray){
        Glide.with(this)
            .load(byteArray)
            .into(this)
    }

    fun timeFormatter (time: Int): String{
        val temp = time / 1000
        val minute = (temp / 60).toString().padStart(2, '0')
        val second = (temp % 60).toString().padStart(2, '0')
        return "$minute:$second"
    }

}