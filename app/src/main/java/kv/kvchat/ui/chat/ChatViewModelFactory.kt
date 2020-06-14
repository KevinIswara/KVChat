package kv.kvchat.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kv.kvchat.data.repository.ChatRepository
import kv.kvchat.data.repository.UserRepository

class ChatViewModelFactory(private val userRepository: UserRepository,
                           private val chatRepository: ChatRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(userRepository, chatRepository) as T
    }
}