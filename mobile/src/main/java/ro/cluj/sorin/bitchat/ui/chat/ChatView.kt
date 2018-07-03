package ro.cluj.sorin.bitchat.ui.chat

import android.os.Message
import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.MvpBase
import ro.cluj.sorin.bitchat.model.ChatMessage

/**
 * Created by sorin on 12.05.18.
 */

interface ChatView : MvpBase.View {
  fun showUserIsLoggedIn(user: FirebaseUser)

  fun showUserIsLoggedOut()

  fun showNearbyChatDisconnected()

  fun showNearbyChatConnected()

  fun showAddedMessage(chatMessage: ChatMessage)
}