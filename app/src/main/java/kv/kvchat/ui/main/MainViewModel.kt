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

    val friends: MutableLiveData<ArrayList<User>> = userRepository.getFriends()

    private var imageUploadResponse: MutableLiveData<NetworkingResponse> = MutableLiveData()

    private var chatFriends: MutableLiveData<ArrayList<User>> = MutableLiveData()

    var imageUri: Uri? = null
    var name: String? = null
    var username: String? = null

    fun init() {
        imageUploadResponse = userRepository.getImageUpdateResponse()
    }

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

    fun getChatFriendsFromFirebase(username: String) {
        chatFriends = chatRepository.getChatFriends(username)
    }

    fun getChatFriends(): MutableLiveData<ArrayList<User>> {
        return chatFriends
    }
}