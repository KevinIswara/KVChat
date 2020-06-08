package kv.kvchat.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kv.kvchat.ChatApplication
import kv.kvchat.data.model.Chat
import kv.kvchat.data.model.User
import kv.kvchat.data.repository.ChatRepository
import kv.kvchat.data.repository.UserRepository

class ChatViewModel(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var friendData: MutableLiveData<User> = MutableLiveData()
    private var chats: MutableLiveData<ArrayList<Chat>> = MutableLiveData()

    var msgText: MutableLiveData<String> = MutableLiveData()

    init {
        chats.value = arrayListOf()
    }

    fun getChatList(): MutableLiveData<ArrayList<Chat>> {
        return chats
    }

    fun getFriends(): MutableLiveData<User> {
        return friendData
    }

    fun getFriendDataFromFirebase(username: String) {
        friendData = userRepository.getFriendData(username)
    }

    fun getChatData(username: String, friendUsername: String) {
        if (username.isNotBlank() && friendUsername.isNotBlank()) {
            chats = chatRepository.readMessage(username, friendUsername)
        }
    }

    fun sendMessage() {
        if (msgText.value != "" && friendData.value != null) {
            chatRepository.sendMessage(
                ChatApplication.getUser().username.toString(),
                friendData.value?.username.toString(), msgText.value ?: ""
            )
        }
        msgText.value = ""
    }
}