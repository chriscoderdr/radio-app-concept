package me.cristiangomez.radioappconcept.ui.player

import android.content.Context
import androidx.lifecycle.ViewModel
import me.cristiangomez.radioappconcept.data.repository.spotify.ArtistRepository

class PlayerViewModel() : ViewModel() {
    // TODO: Implement the ViewModel
    var metaDataLiveData: RadioMetaDataLiveData? = null
    val artistInfoLiveData = ArtistInfoLiveData()

    fun getMetaDataLiveData(context: Context): RadioMetaDataLiveData? {
        if (metaDataLiveData == null) {
            metaDataLiveData = RadioMetaDataLiveData(context)
        }
        return metaDataLiveData
    }

    fun updateArtist(artistsNames: String?, context: Context) {
        val artist = artistsNames?.split("ft", " & ", ignoreCase = true)?.firstOrNull()
        if (artist != null && !artist.isBlank()) {
            ArtistRepository(context).searchArtist(artist, {
                artistInfoLiveData.postValue(it.firstOrNull())
            }, {
                artistInfoLiveData.postValue(null)
            })
        } else {
            artistInfoLiveData.postValue(null)
        }
    }
}
