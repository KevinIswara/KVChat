package kv.kvchat.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kv.kvchat.data.model.User
import kv.kvchat.data.repository.ChatRepository
import kv.kvchat.data.repository.UserRepository

class ChatViewModel(private val userRepository: UserRepository,
                    private val chatRepository: ChatRepository): ViewModel() {

    var friendData: MutableLiveData<User> = MutableLiveData()

    var userData: MutableLiveData<User> = userRepository.getUserData()

    var msgText: MutableLiveData<String> = MutableLiveData()

    fun getFriendData(username: String) {
        friendData = userRepository.getFriendData(username)
    }

    fun sendMessage() {
        if (msgText.value != "" && userData.value != null && friendData.value != null) {
            chatRepository.sendMessage(userData.value?.username.toString(),
                friendData.value?.username.toString(), msgText.value?: "")
        }
        msgText.value = ""
    }
}