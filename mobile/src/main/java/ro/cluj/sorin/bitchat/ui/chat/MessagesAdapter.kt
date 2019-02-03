package ro.cluj.sorin.bitchat.ui.chat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.listitem_incoming_message.view.*
import kotlinx.android.synthetic.main.listitem_my_message.view.*
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.model.ChatMessage
import ro.cluj.sorin.bitchat.utils.fromMillisToTimeString

private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2

/**
 * Created by sorin on 12.05.18.
 */
class ConversationAdapter : RecyclerView.Adapter<MessageViewHolder>() {
    private val chatMessages: ArrayList<ChatMessage> = ArrayList()

    fun addItem(chatMessage: ChatMessage) {
        if (chatMessages.map { it.messageId }.contains(chatMessage.messageId)) return
        chatMessages.add(chatMessage)
        notifyItemInserted(chatMessages.indexOf(chatMessage))
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = chatMessages[position]
        return if (message.isSending) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.listitem_my_message, parent, false))
        } else {
            OtherMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.listitem_incoming_message, parent, false))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) = holder.bind(chatMessages[position])

    inner class MyMessageViewHolder(view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.txtMyMessage
        private var timeText: TextView = view.txtMyMessageTime

        override fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.message
            timeText.text = chatMessage.time.fromMillisToTimeString()
        }
    }

    inner class OtherMessageViewHolder(view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.txtOtherMessage
        private var userText: TextView = view.txtOtherUser
        private var timeText: TextView = view.txtOtherMessageTime

        override fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.message
            userText.text = chatMessage.userName
            timeText.text = chatMessage.time.fromMillisToTimeString()
        }
    }
}

open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(chatMessage: ChatMessage) {}
}