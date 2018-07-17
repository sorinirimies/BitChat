package ro.cluj.sorin.bitchat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatMessage(
  val messageId: String,
  val groupId: String,
  val userId: String,
  val userName: String?,
  var isSending: Boolean,
  val message: String,
  val time: Long
) : Parcelable