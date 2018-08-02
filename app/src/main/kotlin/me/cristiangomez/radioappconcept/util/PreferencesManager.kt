package me.cristiangomez.radioappconcept.util

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.data.pojo.spotify.TokenResponse

class PreferencesManager(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE
    )

    fun isPlaying(): Boolean {
        return sharedPreferences.getBoolean(PREFERENCE_IS_PLAYING, false)
    }

    fun setIsPlaying(isPlaying: Boolean) {
        sharedPreferences.edit().putBoolean(PREFERENCE_IS_PLAYING, isPlaying)
                .apply()
    }

    fun setVolume(volume: Float) {
        sharedPreferences.edit().putFloat(PREFERENCE_VOLUME, volume)
                .apply()
    }

    fun getVolume(): Float {
        return sharedPreferences.getFloat(PREFERENCE_VOLUME, 100f)
    }

    fun setSpotifyAuthToken(token: TokenResponse) {
        sharedPreferences.edit().putString(PREFERENCE_SPOTIFY_API_AUTH_TOKEN,
                Moshi.Builder().build().adapter<TokenResponse>(TokenResponse::class.java)
                        .toJson(token))
                .apply()
    }

    fun getSpotifyAuthToken(): TokenResponse? {
        val adapter = Moshi.Builder().build().adapter<TokenResponse>(TokenResponse::class.java)
        val tokenStr = sharedPreferences.getString(PREFERENCE_SPOTIFY_API_AUTH_TOKEN,
                "")
        if (tokenStr.isBlank()) {
            return null
        }
        return adapter.fromJson(tokenStr)
    }

    fun setMetaData(metaData: String?) {
        sharedPreferences
                .edit()
                .putString(PREFERENCE_META_DATA, metaData)
                .apply()
    }

    fun getMetadata(): String {
        return sharedPreferences
                .getString(PREFERENCE_META_DATA,
                        "")
    }

    companion object {
        const val PREFERENCE_IS_PLAYING = "IS_PLAYING"
        const val PREFERENCE_VOLUME = "VOLUME"
        const val PREFERENCE_SPOTIFY_API_AUTH_TOKEN = "SPOTIFY_API_AUTH_TOKEN"
        const val PREFERENCE_META_DATA = "PLAYER_META_DATA"
    }
}