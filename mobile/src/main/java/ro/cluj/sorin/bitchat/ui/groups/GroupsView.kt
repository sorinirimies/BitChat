package ro.cluj.sorin.bitchat.ui.groups

import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.MvpBase
import ro.cluj.sorin.bitchat.model.ChatGroup

/**
 * Created by sorin on 12.05.18.
 */
interface GroupsView : MvpBase.View {

  fun showChatGroupCreated(group: ChatGroup)

  fun showChatGroupEdited(group: ChatGroup)

  fun showChatGroupDeleted(group: ChatGroup)

  fun showUserIsLoggedIn(user: FirebaseUser)

  fun showUserIsLoggedOut()
}