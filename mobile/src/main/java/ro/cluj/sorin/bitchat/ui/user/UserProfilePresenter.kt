package ro.cluj.sorin.bitchat.ui.user

import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.BasePresenter
import ro.cluj.sorin.bitchat.utils.toBitChatUser

class UserProfilePresenter : BasePresenter<UserProfileView>() {
  fun showUserLoginFailed(msg: String?) {
    view.showUserLoggedInFailed(msg)
  }

  fun userIsLoggedIn(user: FirebaseUser) {
    view.showUserIsLoggedIn(user)
  }

  fun userIsLoggedOut() {
    view.showUserIsLoggedOut()
  }

  fun logoutUser() {
    view.logoutUser()
  }

  fun createOrUpdateBitChatUser(user: FirebaseUser) {
    view.createOrUpdateBitChatUser(user.toBitChatUser())
  }
}