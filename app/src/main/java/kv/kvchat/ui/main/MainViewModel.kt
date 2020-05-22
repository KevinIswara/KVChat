package kv.kvchat.ui.main

import androidx.lifecycle.ViewModel
import kv.kvchat.data.auth.UserRepository

class MainViewModel(private val repository: UserRepository): ViewModel() {

    fun getUserData() = repository.getUserData()
}