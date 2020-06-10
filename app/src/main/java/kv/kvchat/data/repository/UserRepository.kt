package kv.kvchat.data.repository

import android.net.Uri
import kv.kvchat.data.firebase.FirebaseSource
import kv.kvchat.data.firebase.NetworkingResponse

class UserRepository(private val firebase: FirebaseSource) {

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(username: String, name: String, email: String, password: String) =
        firebase.register(username, name, email, password)

    fun currentUser() = firebase.currentUser()

    fun getUserData() = firebase.getUserData()

    fun logout() = firebase.logout()

    fun uploadImage(imageUri: Uri, fileExtension: String) =
        firebase.uploadImage(imageUri, fileExtension)

    fun getImageUpdateResponse() = firebase.getImageUpdateResponse()

    fun getUserDataResponse() = firebase.userDataResponse

    fun setUserDataResponse(response: NetworkingResponse) = firebase.setUserDataResponse(response)

    fun changeName(name: String) = firebase.changeName(name)

    fun getFriends() = firebase.getFriendList()

    fun resetPassword(email: String) = firebase.resetPassword(email)

    fun getPasswordResetResponse() = firebase.getResetPasswordResponse()

    fun getFriendData(username: String) = firebase.getFriendData(username)

    fun setUserStatus(status: String) = firebase.setUserStatus(status)
}