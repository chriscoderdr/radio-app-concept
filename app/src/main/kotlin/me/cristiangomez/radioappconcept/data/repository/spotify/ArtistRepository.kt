package me.cristiangomez.radioappconcept.data.repository.spotify

import android.content.Context
import me.cristiangomez.radioappconcept.data.SpotifyApiBuilder
import me.cristiangomez.radioappconcept.data.model.Artist
import me.cristiangomez.radioappconcept.data.pojo.spotify.ArtistSearchResponse
import me.cristiangomez.radioappconcept.data.repository.ArtistRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistRepository(val context: Context) : ArtistRepository {

    override fun searchArtist(name: String, onSuccess: (List<Artist>) -> Unit,
                              onError: () -> Unit) {
        return SpotifyApiBuilder.getSpotifyApi(context)
                .searchArtist(query = name)
                .enqueue(object : Callback<ArtistSearchResponse> {
                    override fun onFailure(call: Call<ArtistSearchResponse>?, t: Throwable?) {
                        onError()
                    }

                    override fun onResponse(call: Call<ArtistSearchResponse>?, response: Response<ArtistSearchResponse>?) {
                        response?.body()?.artists?.items?.let {
                            onSuccess(it)
                        }
                    }
                })
    }
}