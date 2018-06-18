package ro.cluj.sorin.bitchat.ui.groups

import com.google.firebase.firestore.FirebaseFirestore
import ro.cluj.sorin.bitchat.BasePresenter
import ro.cluj.sorin.bitchat.model.ChatGroup

class GroupsPresenter(private val db: FirebaseFirestore) : BasePresenter<GroupsView>() {

  fun createChatGroup(group: ChatGroup) {
    db.collection("group").document(group.name).set(group).addOnSuccessListener {
      view.showChatGroupCreated(group)
    }
  }

  fun deleteChatGroup(group: ChatGroup) {
    db.collection("group").document(group.name).delete().addOnSuccessListener {
      view.showChatGroupDeleted(group)
    }
  }
}