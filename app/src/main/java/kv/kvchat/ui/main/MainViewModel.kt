package kv.kvchat.ui.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kv.kvchat.data.auth.User
import kv.kvchat.data.auth.UserRepository
import kv.kvchat.data.firebase.NetworkingResponse

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private var user: MutableLiveData<User> = MutableLiveData()

    val friends : MutableLiveData<ArrayList<User>> = repository.getFriends()

    private var imageUploadResponse: MutableLiveData<NetworkingResponse> = MutableLiveData()

    var imageUri: Uri? = null
    var name: String? = null
    var username: String? = null

    fun init() {
        imageUploadResponse = repository.getImageUpdateResponse()
        user = repository.getUserData()
    }

    fun logout() {
        repository.logout()
    }

    fun uploadProfilePicture(fileExtension: String) {
        imageUri?.let {
            repository.uploadImage(it, fileExtension)
        }
    }

    fun changeName(name: String) {
        repository.changeName(name)
    }

    fun getUser(): MutableLiveData<User> {
        return user
    }

    fun getImageUpdateResponse(): MutableLiveData<NetworkingResponse> {
        return imageUploadResponse
    }

    fun setStatus(status: Int) {
        imageUploadResponse.value?.status = status
    }
}