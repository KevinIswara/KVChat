package kv.kvchat.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable

class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }


    fun login(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()

                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun register(username: String, name: String, email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful) {
                    val userId = currentUser()?.uid
                    userId?.let { userId ->
                        val reference = firebaseDatabase.getReference("Users").child(userId)
                        val map: HashMap<String, String> = hashMapOf("username" to username, "name" to name, "imageUrl" to "default")

                        reference.setValue(map).addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                emitter.onComplete()
                            }
                        }
                    }

                } else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

}