package me.cristiangomez.radioappconcept.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PlayerMetadataBroadcastReceiver(
        private val onNewMetaData: (metaData: String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(contet: Context?, intent: Intent?) {
        when {
            intent?.action.equals(ACTION_NEW_META_DATA) ->
                onNewMetaData(intent!!.getStringExtra(EXTRA_META_DATA))
        }
    }

    companion object {
        const val ACTION_NEW_META_DATA = "ACTION_NEW_META_DATA"
        const val EXTRA_META_DATA = "EXTRA_METADATA"
    }
}