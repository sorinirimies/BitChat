package ro.cluj.sorin.bitchat.ui.chat

import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.BasePresenter

/**
 * Created by sorin on 12.05.18.
 */

class ChatPresenter : BasePresenter<ChatView>() {
  fun userIsLoggedIn(user: FirebaseUser) {
    view.showUserIsLoggedIn(user)
  }

  fun userIsLoggedOut() {
    view.showUserIsLoggedOut()
  }

}