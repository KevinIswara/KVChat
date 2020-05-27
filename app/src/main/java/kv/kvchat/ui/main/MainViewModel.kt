package kv.kvchat.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kv.kvchat.data.auth.User
import kv.kvchat.data.auth.UserRepository

class MainViewModel(private val repository: UserRepository): ViewModel() {

    fun getUserData() = repository.getUserData()

    val friends : MutableLiveData<ArrayList<User>> = repository.getFriends()
}