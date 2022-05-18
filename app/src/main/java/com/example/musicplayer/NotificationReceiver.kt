package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val musicPlayerServiceIntent = Intent(context, MusicPlayerService::class.java)
            musicPlayerServiceIntent.action = intent.action
            context?.startService(musicPlayerServiceIntent)
        }
    }
}