package ro.cluj.sorin.bitchat.ui.user

import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.MvpBase
import ro.cluj.sorin.bitchat.model.User

interface UserProfileView : MvpBase.View {

  fun showUserIsLoggedIn(user: FirebaseUser)

  fun showUserLoggedInFailed(msg: String?)

  fun showUserIsLoggedOut()

  fun logoutUser()

  fun createOrUpdateBitChatUser(bitChatUser: User)
}