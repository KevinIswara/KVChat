package kv.kvchat.data.firebase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.Completable
import kv.kvchat.data.model.User

class FirebaseSource {

    var user: MutableLiveData<User> = MutableLiveData()

    var chatUser: MutableLiveData<User> = MutableLiveData()

    var friends: MutableLiveData<ArrayList<User>> = MutableLiveData()
  
    var imageUploadResponse: MutableLiveData<NetworkingResponse> = MutableLiveData()

    companion object {
        const val IMAGE_UPLOAD_SUCCESS = 11
        const val IMAGE_UPLOAD_FAILED = 10
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
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

                        currUserReference()?.setValue(map)?.addOnCompleteListener{ task ->
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

    private fun currUserReference(): DatabaseReference? {
        val userId = currentUser()?.uid
        userId?.let {
            return firebaseDatabase.getReference("Users").child(it)
        }
        return null
    }

    private fun userReference(): DatabaseReference? = firebaseDatabase.getReference("Users")

    private fun chatReference(): DatabaseReference? = firebaseDatabase.getReference("Chats")

    private fun storageReference(): StorageReference? {
        return firebaseStorage.getReference("uploads")
    }

    fun uploadImage(filePath: Uri, fileExtension: String){
        val ref = storageReference()?.child("uploads/" +
                System.currentTimeMillis().toString() + "." + fileExtension)
        val uploadTask = ref?.putFile(filePath)
        var response = NetworkingResponse()

        uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot,
                Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                addProfilePictureToDB(downloadUri.toString())
                val response = NetworkingResponse(status = IMAGE_UPLOAD_SUCCESS)
                imageUploadResponse.postValue(response)
            } else {
                response.status = IMAGE_UPLOAD_FAILED
                response.title = "Failed"
                response.message = "Profile picture has not been changed!"
                imageUploadResponse.postValue(response)
            }
        }?.addOnFailureListener{ e ->
            response.status = IMAGE_UPLOAD_FAILED
            response.title = "Failed"
            response.message = e.message
            imageUploadResponse.postValue(response)
        }
    }

    private fun addProfilePictureToDB(downloadUri: String) {
        val data = HashMap<String, Any>()
        data["imageUrl"] = downloadUri
        currUserReference()?.updateChildren(data)
    }

    fun changeName(name: String) {
        val data = HashMap<String, Any>()
        data["name"] = name
        currUserReference()?.updateChildren(data)
    }

    fun getImageUpdateResponse(): MutableLiveData<NetworkingResponse> {
        return imageUploadResponse
    }

    fun getUserData(): MutableLiveData<User> {
        currUserReference()?.addValueEventListener(object : ValueEventListener {
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

    fun getFriendData(username: String): MutableLiveData<User> {
        val ref = userReference()?.orderByChild("username")?.equalTo(username)

        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val userData = snapshot.getValue(User::class.java)
                    chatUser.postValue(userData)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        return chatUser
    }

    fun getFriendList(): MutableLiveData<ArrayList<User>> {
        val reference = firebaseDatabase.getReference("Users")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val friendList : ArrayList<User> = ArrayList()
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

    fun sendMessage(sender: String, receiver: String, message: String) {
        val map: HashMap<String, String> = hashMapOf("sender" to sender, "receiver" to receiver,
            "message" to message)

        chatReference()?.push()?.setValue(map)
    }
}

data class NetworkingResponse(var status: Int = 0,
                              var message: String? = null,
                              var title: String? = null)
