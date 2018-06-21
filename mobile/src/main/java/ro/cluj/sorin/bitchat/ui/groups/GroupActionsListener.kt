package ro.cluj.sorin.bitchat.ui.groups

import ro.cluj.sorin.bitchat.model.ChatGroup
/**
 * Created by sorin on 12.05.18.
 */
interface GroupActionsListener {
  fun selectGroup(group: ChatGroup)
  fun deleteGroup(group: ChatGroup)
  fun editGroup(group: ChatGroup)
}