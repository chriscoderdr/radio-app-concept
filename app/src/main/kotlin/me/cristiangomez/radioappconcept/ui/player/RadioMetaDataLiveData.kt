package me.cristiangomez.radioappconcept.ui.player

import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import me.cristiangomez.radioappconcept.data.model.MetaData
import me.cristiangomez.radioappconcept.receiver.PlayerMetadataBroadcastReceiver
import me.cristiangomez.radioappconcept.util.PreferencesManager

class RadioMetaDataLiveData(val context: Context) : LiveData<MetaData>() {
    private val localBroadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(context)

    private val metaDataReceiver = PlayerMetadataBroadcastReceiver { metaDataStr: String ->
        postValue(parseMetaData(metaDataStr))
    }

    override fun onActive() {
        super.onActive()
        postValue(parseMetaData(PreferencesManager(context).getMetadata()))
        val intentFilter = IntentFilter()
        intentFilter.addAction(PlayerMetadataBroadcastReceiver.ACTION_NEW_META_DATA)
        localBroadcastManager.registerReceiver(metaDataReceiver, intentFilter)
    }

    override fun onInactive() {
        super.onInactive()
        localBroadcastManager.unregisterReceiver(metaDataReceiver)
    }

    private fun parseMetaData(metaDataStr: String): MetaData {
        val metaList = metaDataStr.split(" - ")
        val artist = metaList.firstOrNull()
        val songTitle = metaList.getOrNull(1)
        return MetaData(songTitle, artist)
    }
}