package kv.kvchat.data.model

class Chat(
    var sender: String = "",
    var receiver: String = "",
    var message: String = "",
    var isseen: Boolean = false
)