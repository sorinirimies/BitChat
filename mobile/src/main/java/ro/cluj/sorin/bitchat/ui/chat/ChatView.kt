package ro.cluj.sorin.bitchat.ui.chat

import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.MvpBase

/**
 * Created by sorin on 12.05.18.
 */

interface ChatView: MvpBase.View {
  fun showUserIsLoggedIn(user: FirebaseUser)

  fun showUserIsLoggedOut()
}