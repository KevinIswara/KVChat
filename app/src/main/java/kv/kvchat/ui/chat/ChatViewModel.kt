package kv.kvchat.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kv.kvchat.data.auth.User
import kv.kvchat.data.auth.UserRepository

class ChatViewModel(private val repository: UserRepository): ViewModel() {

    var userData: MutableLiveData<User> = MutableLiveData()

    fun getFriendData(username: String) {
        userData = repository.getFriendData(username)
    }
}