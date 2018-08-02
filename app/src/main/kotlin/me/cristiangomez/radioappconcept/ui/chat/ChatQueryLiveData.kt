package me.cristiangomez.radioappconcept.ui.chat

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*

class ChatQueryLiveData(val query: Query) : LiveData<QuerySnapshot>() {
    private var listenerRegistration: ListenerRegistration? = null
    private val listener = ChatListener()

    constructor(collectionReference: CollectionReference) : this(query = collectionReference) {
    }

    override fun onActive() {
        super.onActive()
        listenerRegistration = query.addSnapshotListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    private inner class ChatListener : EventListener<QuerySnapshot> {
        override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
            value = snapshot
        }
    }
}