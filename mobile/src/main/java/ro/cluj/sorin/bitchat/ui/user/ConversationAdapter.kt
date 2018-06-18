package ro.cluj.sorin.bitchat.ui.user

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.listitem_incoming_message.view.txtOtherMessage
import kotlinx.android.synthetic.main.listitem_incoming_message.view.txtOtherMessageTime
import kotlinx.android.synthetic.main.listitem_incoming_message.view.txtOtherUser
import kotlinx.android.synthetic.main.listitem_my_message.view.txtMyMessage
import kotlinx.android.synthetic.main.listitem_my_message.view.txtMyMessageTime
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.model.Message
import ro.cluj.sorin.bitchat.utils.fromMillisToTimeString

private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2

class ConversationAdapter : RecyclerView.Adapter<MessageViewHolder>() {
  private val messages: ArrayList<Message> = ArrayList()

  fun addMessage(message: Message) {
    messages.add(message)
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return messages.size
  }

  override fun getItemViewType(position: Int): Int {
    val message = messages[position]
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

  override fun onBindViewHolder(holder: MessageViewHolder, position: Int) = holder.bind(messages[position])

  inner class MyMessageViewHolder(view: View) : MessageViewHolder(view) {
    private var messageText: TextView = view.txtMyMessage
    private var timeText: TextView = view.txtMyMessageTime

    override fun bind(message: Message) {
      messageText.text = message.message
      timeText.text = message.time.fromMillisToTimeString()
    }
  }

  inner class OtherMessageViewHolder(view: View) : MessageViewHolder(view) {
    private var messageText: TextView = view.txtOtherMessage
    private var userText: TextView = view.txtOtherUser
    private var timeText: TextView = view.txtOtherMessageTime

    override fun bind(message: Message) {
      messageText.text = message.message
      userText.text = message.userName
      timeText.text = message.time.fromMillisToTimeString()
    }
  }
}

open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  open fun bind(message: Message) {}
}