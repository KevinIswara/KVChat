package kv.kvchat.data.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Completable
import kv.kvchat.data.auth.User

class FirebaseSource {

    var user: MutableLiveData<User> = MutableLiveData()

    var friends: MutableLiveData<ArrayList<User>> = MutableLiveData()

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
                        val map: HashMap<String, String> = hashMapOf("username" to username, "name" to name, "imageUrl" to "default")

                        userReference()?.setValue(map)?.addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                emitter.onComplete()
                            }
                        }
                } else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

    private fun userReference(): DatabaseReference? {
        val userId = currentUser()?.uid
        userId?.let {
            return firebaseDatabase.getReference("Users").child(it)
        }
        return null
    }

    fun getUserData(): MutableLiveData<User> {
        userReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userData = dataSnapshot.getValue(User::class.java)
                    user.postValue(userData)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        return user
    }

    fun getFriendList(): MutableLiveData<ArrayList<User>> {
        val reference = firebaseDatabase.getReference("Users")
        val friendList : ArrayList<User> = ArrayList()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val userItem = snapshot.getValue(User::class.java)
                    userItem?.let {
                        if (!snapshot.key.equals(currentUser()?.uid)) {
                            friendList.add(it)
                        }
                    }
                }
                friends.postValue(friendList)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        return friends
    }
}