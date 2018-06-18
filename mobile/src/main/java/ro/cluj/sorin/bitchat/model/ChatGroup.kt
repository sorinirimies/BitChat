package ro.cluj.sorin.bitchat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatGroup(val name: String) : Parcelable