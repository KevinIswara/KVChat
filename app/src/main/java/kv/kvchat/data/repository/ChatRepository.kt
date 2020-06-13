package kv.kvchat.data.repository

import kv.kvchat.data.firebase.FirebaseSource

class ChatRepository(private val firebase: FirebaseSource) {

    fun sendMessage(sender: String, receiver: String, message: String) =
        firebase.sendMessage(sender, receiver, message)

    fun readMessage(myUsername: String, friendUsername: String) =
        firebase.readMessage(myUsername, friendUsername)

    fun seenMessage(myUsername: String, friendUsername: String) =
        firebase.seenMessage(myUsername, friendUsername)

    fun deleteSeenMessage() = firebase.deleteSeenMessage()

    fun getChatFriends(myUsername: String) = firebase.getChatFriendList(myUsername)

    fun setNotify(value: Boolean) = firebase.setNotify(value)
}