package ro.cluj.sorin.bitchat.ui.chat

import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.BasePresenter

class ChatPresenter : BasePresenter<ChatView>() {
  fun userIsLoggedIn(user: FirebaseUser) {
    view.showUserIsLoggedIn(user)
  }

  fun userIsLoggedOut() {
    view.showUserIsLoggedOut()
  }

}