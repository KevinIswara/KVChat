package kv.kvchat.data.firebase

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.*
import io.reactivex.Completable
import kv.kvchat.data.auth.User

class FirebaseSource {

    var user: MutableLiveData<User> = MutableLiveData()

    var imageUploadResponse: MutableLiveData<NetworkingResponse> = MutableLiveData()

    companion object {
        val IMAGE_UPLOAD_SUCCESS = 11
        val IMAGE_UPLOAD_FAILED = 10
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
                getUserData()
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
        userReference()?.updateChildren(data)
    }

    fun getImageUpdateResponse(): MutableLiveData<NetworkingResponse> {
        return imageUploadResponse
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
}

data class NetworkingResponse(var status: Int = 0,
                              var message: String? = null,
                              var title: String? = null)