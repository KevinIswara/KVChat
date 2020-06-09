package kv.kvchat.ui.auth

interface AuthListener {
    fun onStarted()
    fun onSuccess(code: Int)
    fun onFailure(message: String)
}