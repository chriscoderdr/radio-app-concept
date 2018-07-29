package me.cristiangomez.radioappconcept.data

import android.util.Base64
import io.reactivex.Observable
import me.cristiangomez.radioappconcept.BuildConfig
import me.cristiangomez.radioappconcept.data.pojo.spotify.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface SpotifyAuthApi {
    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    fun authenticate(@Field("grant_type") grantType: String = "client_credentials",
                     @Header("Authorization") authorization: String
                     = "Basic " + Base64.encodeToString((BuildConfig.SPOTIFY_API_CLIENT_ID + ":" +
                             BuildConfig.SPOTIFY_API_CLIENT_SECRET).toByteArray(), Base64.NO_WRAP)):
            Observable<TokenResponse>
}