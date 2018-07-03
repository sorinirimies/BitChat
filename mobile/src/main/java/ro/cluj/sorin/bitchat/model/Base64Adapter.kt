package ro.cluj.sorin.bitchat.model

import android.util.Base64

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class Base64Adapter {
  @FromJson
  internal fun fromJson(string: String): ByteArray = Base64.decode(string, Base64.NO_WRAP)

  @ToJson
  fun toJson(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
}