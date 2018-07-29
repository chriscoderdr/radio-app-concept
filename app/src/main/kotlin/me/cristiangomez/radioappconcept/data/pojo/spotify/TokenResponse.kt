package me.cristiangomez.radioappconcept.data.pojo.spotify

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class TokenResponse(@Json(name = "access_token") var accessToken: String?,
                         @Json(name = "token_type") var tokenType: String?,
                         @Json(name = "expires_in") var expiresIn: Long?,
                         val createdAt: Long = Date().time) {
}