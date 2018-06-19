package ro.cluj.sorin.bitchat.ui.groups

import ro.cluj.sorin.bitchat.model.ChatGroup

interface GroupActionsListener {
  fun selectGroup(group: ChatGroup)
  fun deleteGroup(group: ChatGroup)
  fun editGroup(group: ChatGroup)
}