package me.cristiangomez.radioappconcept.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(var id: String, var name: String, var images: List<ArtistImage>) {
}