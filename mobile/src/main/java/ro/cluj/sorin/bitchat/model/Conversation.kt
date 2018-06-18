package ro.cluj.sorin.bitchat.model

data class Conversation(val id: String, val sender: User, val receiver: User, val messages: List<Message>)