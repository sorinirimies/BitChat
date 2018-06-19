package ro.cluj.sorin.bitchat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatGroup(val id: String, var name: String) : Parcelable