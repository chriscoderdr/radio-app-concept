package me.cristiangomez.radioappconcept.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ChatMessage(var message: String? = null, var author: User? = null,
                       @ServerTimestamp var sentAt: Timestamp? = null) {
}