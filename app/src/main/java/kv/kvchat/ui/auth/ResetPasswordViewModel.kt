package kv.kvchat.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kv.kvchat.data.firebase.FirebaseSource
import kv.kvchat.data.firebase.NetworkingResponse
import kv.kvchat.data.repository.UserRepository

class ResetPasswordViewModel(private val repository: UserRepository) : ViewModel() {

    private var resetPasswordResponse: MutableLiveData<NetworkingResponse> =
        repository.getPasswordResetResponse()

    var email: String? = null

    fun getResetPasswordResponse(): MutableLiveData<NetworkingResponse> {
        return resetPasswordResponse
    }

    fun resetPassword() {
        if (!email.isNullOrBlank()) {
            repository.resetPassword(email ?: "")
        } else {
            resetPasswordResponse.value = NetworkingResponse(
                status = FirebaseSource.RESET_PASSWORD_FAILED,
                title = "Failed!", message = "Email can't be blank!"
            )
        }
    }

    fun setStatus(status: Int) {
        resetPasswordResponse.value?.status = status
    }
}