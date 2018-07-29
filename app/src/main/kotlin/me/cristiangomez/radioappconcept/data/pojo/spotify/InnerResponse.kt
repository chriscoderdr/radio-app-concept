package me.cristiangomez.radioappconcept.data.pojo.spotify

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InnerResponse<T>(var items: List<T>, var limit: Int, var next: String? = null,
                            var total: Int) {
}