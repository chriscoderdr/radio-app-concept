package me.cristiangomez.radioappconcept.data

import me.cristiangomez.radioappconcept.data.pojo.spotify.ArtistSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyApi {

    @GET("v1/search")
    fun searchArtist(@Query("type") type: String = "artist",
                     @Query("q") query: String): Call<ArtistSearchResponse>
}