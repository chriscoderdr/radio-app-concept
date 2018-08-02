package me.cristiangomez.radioappconcept.data

import android.content.Context
import com.squareup.moshi.Moshi
import me.cristiangomez.radioappconcept.BuildConfig
import me.cristiangomez.radioappconcept.data.pojo.spotify.TokenResponse
import me.cristiangomez.radioappconcept.util.PreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class SpotifyApiBuilder {
    companion object {
        fun getSpotifyApi(context: Context): SpotifyApi {
            val spotifyAuthApi = getSpotifyAuthApi()
            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor { chain: Interceptor.Chain? ->
                        if (chain!!.request()!!.url()!!.url()!!.path!!.endsWith("api/token")
                                && chain.request().header("Authorization") != null) {
                            chain.proceed(chain.request())
                        } else {
                            val preferencesManager = PreferencesManager(context)
                            val savedToken = preferencesManager.getSpotifyAuthToken()
                            var token: TokenResponse? = savedToken
                            if (savedToken == null || Date().time >= (savedToken.createdAt + savedToken.expiresIn!! + 500)) {
                                token = spotifyAuthApi.authenticate()
                                        .execute()?.body()
                                if (token != null) {
                                    preferencesManager.setSpotifyAuthToken(token)
                                }
                            }
                            val request = chain.request().newBuilder()
                                    .addHeader("Authorization",
                                            "Bearer " + token!!.accessToken)
                                    .build()
                            chain.proceed(request)
                        }
                    }
                    .build()
            val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BuildConfig.SPOTIFY_API_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder()
                            .build()))
                    .build()
            return retrofit.create(SpotifyApi::class.java)
        }

        private fun getSpotifyAuthApi(): SpotifyAuthApi {
            val okHttpClient = OkHttpClient.Builder()
                    .build()
            val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BuildConfig.SPOTIFY_API_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder()
                            .build()))
                    .build()
            return retrofit.create(SpotifyAuthApi::class.java)
        }
    }
}