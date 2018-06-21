package ro.cluj.sorin.bitchat.utils

import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.model.User

/**
 * Created by sorin on 12.05.18.
 */
fun FirebaseUser.toBitChatUser() = User(this.uid, this.displayName, this.email, this.phoneNumber, this.photoUrl.toString())