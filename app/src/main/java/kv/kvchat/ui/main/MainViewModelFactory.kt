package kv.kvchat.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kv.kvchat.data.repository.ChatRepository
import kv.kvchat.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(userRepository, chatRepository) as T
    }
}