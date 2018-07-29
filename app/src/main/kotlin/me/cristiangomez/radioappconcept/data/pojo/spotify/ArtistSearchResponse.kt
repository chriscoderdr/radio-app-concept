package me.cristiangomez.radioappconcept.data.pojo.spotify

import com.squareup.moshi.JsonClass
import me.cristiangomez.radioappconcept.data.model.Artist

@JsonClass(generateAdapter = true)
data class ArtistSearchResponse(var artists: InnerResponse<Artist>? = null) {
}