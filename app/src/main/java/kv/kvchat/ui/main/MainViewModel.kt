package kv.kvchat.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kv.kvchat.data.auth.User
import kv.kvchat.data.auth.UserRepository

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private var user: MutableLiveData<User> = MutableLiveData()

    var name: String? = "aaa"
    var username: String? = "bbb"

    fun getUserData() {
        user = repository.getUserData()
    }

    fun logout() {
        repository.logout()
    }

    fun getUser(): MutableLiveData<User> {
        return user
    }
}