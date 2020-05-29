package kv.kvchat.data.repository

import kv.kvchat.data.firebase.FirebaseSource

class ChatRepository(private val firebase: FirebaseSource) {

    fun sendMessage(sender: String, receiver: String, message: String) = firebase.sendMessage(sender, receiver, message)
}