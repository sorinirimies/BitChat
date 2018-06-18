package ro.cluj.sorin.bitchat.ui.groups

import ro.cluj.sorin.bitchat.MvpBase
import ro.cluj.sorin.bitchat.model.ChatGroup

interface GroupsView : MvpBase.View {

  fun showChatGroupCreated(group: ChatGroup)
  fun showChatGroupDeleted(group: ChatGroup)
}