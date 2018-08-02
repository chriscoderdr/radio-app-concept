package me.cristiangomez.radioappconcept.ui.chat

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.chat_message_item.*
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.data.model.ChatMessage
import me.cristiangomez.radioappconcept.util.ColorUtil
import me.cristiangomez.radioappconcept.util.md5

class ChatMessagesAdapter(private var chatMessages: List<ChatMessage>) :
        RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_message_item,
                parent, false))
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chatMessages[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        fun bind(chatMessage: ChatMessage) {
            if (chatMessage.message != null && chatMessage.author != null) {
                chat_message_text.text = chatMessage.message
                chat_message_container.setBackgroundColor(
                        ColorUtil.hashToColor(chatMessage.author!!.uuid!!))
                chat_message_author.text = chatMessage.author!!.displayName
                chatMessageSentAt.text = DateUtils.getRelativeDateTimeString(itemView.context,
                        chatMessage.sentAt!!.toDate().time, DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.DAY_IN_MILLIS, 0)
                if (chatMessage.author?.email != null) {
                    val gravatarHash = chatMessage.author?.email?.toLowerCase()?.trim()?.md5()
                    val imageUrl = "https://www.gravatar.com/avatar/$gravatarHash?s=250&d=identicon"
                    Picasso.get().load(imageUrl)
                            .tag(imageUrl)
                            .into(chat_message_author_avatar)
                }
            }
        }

        override val containerView: View?
            get() = itemView
    }
}