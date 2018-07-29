package me.cristiangomez.radioappconcept.data.repository.spotify

import android.content.Context
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.cristiangomez.radioappconcept.data.SpotifyApiBuilder
import me.cristiangomez.radioappconcept.data.model.Artist
import me.cristiangomez.radioappconcept.data.repository.ArtistRepository

class ArtistRepository(val context: Context) : ArtistRepository {
    override fun searchArtist(name: String): Observable<List<Artist>> {
        return SpotifyApiBuilder.getSpotifyApi(context)
                .searchArtist(query = name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it ->
                    it.artists?.items
                }
    }
}