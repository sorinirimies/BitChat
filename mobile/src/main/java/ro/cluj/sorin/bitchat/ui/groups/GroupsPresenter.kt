package ro.cluj.sorin.bitchat.ui.groups

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import ro.cluj.sorin.bitchat.BasePresenter
import ro.cluj.sorin.bitchat.model.ChatGroup

/**
 * Created by sorin on 12.05.18.
 */
class GroupsPresenter(private val db: FirebaseFirestore) : BasePresenter<GroupsView>() {

  fun createChatGroup(group: ChatGroup) {
    db.collection("group").document(group.id).set(group).addOnSuccessListener {
      view.showChatGroupCreated(group)
    }
  }

  fun editChatGroup(group: ChatGroup) {
    db.collection("group").document(group.id).update("name", group.name).addOnSuccessListener {
      view.showChatGroupEdited(group)
    }
  }

  fun deleteChatGroup(group: ChatGroup) {
    db.collection("group").document(group.id).delete().addOnSuccessListener {
      view.showChatGroupDeleted(group)
    }
  }

  fun userIsLoggedIn(user: FirebaseUser) {
    view.showUserIsLoggedIn(user)
  }

  fun userIsLoggedOut() {
    view.showUserIsLoggedOut()
  }
}