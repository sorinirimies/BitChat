package ro.cluj.sorin.bitchat.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun Long.fromMillisToTimeString(): String {
  val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
  return format.format(this)
}