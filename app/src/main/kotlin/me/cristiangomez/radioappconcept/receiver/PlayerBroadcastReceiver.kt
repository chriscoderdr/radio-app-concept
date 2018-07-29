package me.cristiangomez.radioappconcept.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PlayerBroadcastReceiver(private val onPlay: () -> Unit, private val onPause: () -> Unit,
                              private val onStop: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when {
            intent?.action.equals(ACTION_PLAY) -> onPlay()
            intent?.action.equals(ACTION_PAUSE) -> onPause()
            intent?.action.equals(ACTION_STOP) -> onStop()
        }
    }

    companion object {
        const val ACTION_PLAY = "PLAY"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_STOP = "STOP"
    }
}