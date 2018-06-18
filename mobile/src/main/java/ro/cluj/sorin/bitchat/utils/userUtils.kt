package ro.cluj.sorin.bitchat.utils

import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.model.User

fun FirebaseUser.toBitChatUser() = User(this.uid, this.displayName, this.email, this.phoneNumber, this.photoUrl.toString())