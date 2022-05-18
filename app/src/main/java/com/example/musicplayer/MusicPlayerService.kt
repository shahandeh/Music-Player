package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicplayer.MPlayer.getMusicArt
import com.example.musicplayer.MPlayer.serviceActionNext
import com.example.musicplayer.MPlayer.serviceActionPlay
import com.example.musicplayer.MPlayer.serviceActionPrevious
import com.example.musicplayer.MusicPlayerApp.Companion.currentMusicList
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayer
import com.example.musicplayer.MusicPlayerApp.Companion.musicPlayerState
import com.example.musicplayer.MusicPlayerApp.Companion.musicPosition

class MusicPlayerService : Service(), MediaPlayer.OnCompletionListener {

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action != null) {
            when (action) {
                ACTION_PREVIOUS -> serviceActionPrevious(this)
                ACTION_PLAY -> serviceActionPlay()
                ACTION_NEXT -> serviceActionNext(this)
                UPDATE_NOTIFICATION -> showNotification()
            }
        }
        musicPlayer.setOnCompletionListener(this@MusicPlayerService)
        notificationChannel()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID_1,
                "My Music Player",
                NotificationManager.IMPORTANCE_HIGH
            )
            val systemService = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            systemService.createNotificationChannel(serviceChannel)
        }
        showNotification()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification() {

        val intent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val prevIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ACTION_PREVIOUS)
        val prevPending = PendingIntent.getBroadcast(
            this, 0, prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val pauseIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ACTION_PLAY)
        val pausePending = PendingIntent.getBroadcast(
            this, 0, pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val nextIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ACTION_NEXT)
        val nextPending = PendingIntent.getBroadcast(
            this, 0, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mediaSessionCompat = MediaSessionCompat(baseContext, "My Audio")
        mediaSessionCompat.isActive = true
        val temp = getMusicArt()
        val thumbnail = if (temp != null) BitmapFactory.decodeByteArray(temp, 0, temp.size)
        else BitmapFactory.decodeResource(resources, R.drawable.ic_android)

        val playPause = if (musicPlayerState) R.drawable.ic_play
        else R.drawable.ic_pause

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_play)
            .setLargeIcon(thumbnail)
            .setContentTitle(currentMusicList[musicPosition].title)
            .setContentTitle(currentMusicList[musicPosition].artist)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_previous, "Previous", prevPending)
            .addAction(playPause, "Pause", pausePending)
            .addAction(R.drawable.ic_next, "Next", nextPending)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(123, notification)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        serviceActionNext(this)
        showNotification()
    }

}