package ro.cluj.sorin.bitchat.utils

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

fun hasPermissions(context: Context, vararg permissions: String): Boolean {
  for (permission in permissions) {
    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
      return false
    }
  }
  return true
}