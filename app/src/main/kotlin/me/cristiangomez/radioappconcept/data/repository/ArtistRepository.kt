package me.cristiangomez.radioappconcept.data.repository

import io.reactivex.Observable
import me.cristiangomez.radioappconcept.data.model.Artist

interface ArtistRepository {
    fun searchArtist(name: String): Observable<List<Artist>>
}