package me.cristiangomez.radioappconcept.data.repository

import me.cristiangomez.radioappconcept.data.model.Artist

interface ArtistRepository {
    fun searchArtist(name: String, onSuccess: (List<Artist>) -> Unit,
                     onError: () -> Unit)
}