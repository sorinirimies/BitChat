package ro.cluj.sorin.bitchat.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import ro.cluj.sorin.bitchat.model.ChatGroup
import ro.cluj.sorin.bitchat.model.ChatMessage

object MoshiAdapters {
  val moshi: Moshi by lazy {
    val builder = Moshi.Builder()
    builder.build()
  }
  val ChatMessage: JsonAdapter<ChatMessage> = adapter()
  val ChatGroup: JsonAdapter<ChatGroup> = adapter()

  private inline fun <reified T> adapter(): JsonAdapter<T> = moshi.adapter(T::class.java).lenient().indent("  ")
}
