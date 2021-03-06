package kv.kvchat.ui.auth

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kv.kvchat.data.firebase.NetworkingResponse
import kv.kvchat.data.repository.UserRepository

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    companion object {
        const val LOGIN_SUCCESS = 101
        const val SIGNUP_SUCCESS = 102
    }

    //email and password for the input
    var username: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null

    //auth listener
    var authListener: AuthListener? = null

    //disposable to dispose the Completable
    private val disposables = CompositeDisposable()

    val user by lazy {
        repository.currentUser()
    }

    private var userDataResponse = repository.getUserDataResponse()

    //function to perform login
    fun login() {

        //validating email and password
        if (email.isNullOrEmpty()) {
            authListener?.onFailure("Email is required")
            return
        } else if (password.isNullOrEmpty()) {
            authListener?.onFailure("Password is required")
            return
        }

        //authentication started
        authListener?.onStarted()

        //calling login from repository to perform the actual authentication
        val disposable = repository.login(email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //sending a success callback
                authListener?.onSuccess(LOGIN_SUCCESS)
            }, {
                //sending a failure callback
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    //Doing same thing with signup
    fun signup() {
        when {
            username.isNullOrEmpty() -> {
                authListener?.onFailure("Username is required")
                return
            }
            name.isNullOrEmpty() -> {
                authListener?.onFailure("Name is required")
                return
            }
            email.isNullOrEmpty() -> {
                authListener?.onFailure("Email is required")
                return
            }
            password.isNullOrEmpty() -> {
                authListener?.onFailure("Password is required")
                return
            }
            password!!.length < 6 -> authListener?.onFailure("Password must be at least 6 characters")
        }
        authListener?.onStarted()
        val disposable = repository.register(username!!, name!!, email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                authListener?.onSuccess(SIGNUP_SUCCESS)
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun goToSignup(view: View) {
        Intent(view.context, SignupActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun goToLogin(view: View) {
        Intent(view.context, LoginActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun goToResetPassword(view: View) {
        Intent(view.context, ResetPasswordActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun getUserData() {
        authListener?.onStarted()
        val disposable = repository.getUserData()
        disposables.add(disposable)
    }

    fun getUserDataResponse(): MutableLiveData<NetworkingResponse> {
        return userDataResponse
    }

    fun setStatus(status: Int) {
        userDataResponse.value?.status = status
    }

    //disposing the disposables
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}