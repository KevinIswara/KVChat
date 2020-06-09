package kv.kvchat.ui.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kv.kvchat.data.model.User
import kv.kvchat.data.repository.UserRepository
import kv.kvchat.data.firebase.NetworkingResponse
import kv.kvchat.data.repository.ChatRepository

class MainViewModel(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var friends = userRepository.getFriends()

    private var imageUploadResponse = userRepository.getImageUpdateResponse()

    private var chatFriends: MutableLiveData<ArrayList<User>> = MutableLiveData()

    var imageUri: Uri? = null
    var name: String? = null
    var username: String? = null

    fun logout() {
        userRepository.logout()
    }

    fun uploadProfilePicture(fileExtension: String) {
        imageUri?.let {
            userRepository.uploadImage(it, fileExtension)
        }
    }

    fun changeName(name: String) {
        userRepository.changeName(name)
    }

    fun getImageUpdateResponse(): MutableLiveData<NetworkingResponse> {
        return imageUploadResponse
    }

    fun setStatus(status: Int) {
        imageUploadResponse.value?.status = status
    }

    fun getFriends(): MutableLiveData<ArrayList<User>> {
        return friends
    }

    fun getChatFriendsFromFirebase(username: String) {
        chatFriends = chatRepository.getChatFriends(username)
    }

    fun getChatFriends(): MutableLiveData<ArrayList<User>> {
        return chatFriends
    }

    fun resetUserDataResponse() {
        userRepository.setUserDataResponse(NetworkingResponse())
    }
}