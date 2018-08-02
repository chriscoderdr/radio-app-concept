package me.cristiangomez.radioappconcept.ui.chat

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import me.cristiangomez.radioappconcept.data.model.ChatMessage
import java.util.concurrent.Executors

class ChatViewModel : ViewModel() {

    val liveData: ChatQueryLiveData = ChatQueryLiveData(CHAT_MESSAGES_REF)

    val chatMessagesLiveData = MediatorLiveData<List<ChatMessage>>()
    var lastMessageSent: String? = null

    init {
        chatMessagesLiveData.addSource(liveData) { it ->
            if (it != null) {
                val service = Executors.newSingleThreadExecutor()
                service.submit {
                    chatMessagesLiveData.postValue(it.toObjects(ChatMessage::class.java).sortedBy { chatMessage ->
                        chatMessage.sentAt
                    })
                }
            } else {
                chatMessagesLiveData.value = null
            }
        }
    }

    fun sendMessage(chatMessage: ChatMessage, onSuccess: () -> Unit, onError: () -> Unit) {
        if (lastMessageSent != chatMessage.message) {
            CHAT_MESSAGES_REF.add(chatMessage).addOnSuccessListener {
                lastMessageSent = chatMessage.message
                onSuccess()
            }.addOnFailureListener {
                onError()
            }.addOnCanceledListener {
                onError()
            }
        } else {
            onError()
        }
    }


    companion object {
        val CHAT_MESSAGES_REF = FirebaseFirestore.getInstance()
                .collection("publicChannelMessages")
    }
}
