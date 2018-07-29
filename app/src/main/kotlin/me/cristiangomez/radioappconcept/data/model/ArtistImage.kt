package me.cristiangomez.radioappconcept.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistImage(var width: Int?, var height: Int?, var url: String) {
}