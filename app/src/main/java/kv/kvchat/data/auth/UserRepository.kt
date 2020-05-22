package kv.kvchat.data.auth

import kv.kvchat.data.firebase.FirebaseSource

class UserRepository(private val firebase: FirebaseSource) {

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(username: String, name: String, email: String, password: String) = firebase.register(username, name, email, password)

    fun currentUser() = firebase.currentUser()

    fun getUserData() = firebase.getUserData()

    fun logout() = firebase.logout()
}