package kv.kvchat.data.firebase

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kv.kvchat.ChatApplication
import kv.kvchat.data.model.Chat
import kv.kvchat.data.model.Data
import kv.kvchat.data.model.User
import kv.kvchat.R
import kv.kvchat.data.model.Sender
import kv.kvchat.data.network.ApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirebaseSource {

    var user: MutableLiveData<User> = MutableLiveData()

    var chatUser: MutableLiveData<User> = MutableLiveData()

    var friends: MutableLiveData<ArrayList<User>> = MutableLiveData()
    var chats: MutableLiveData<ArrayList<Chat>> = MutableLiveData()
    var chatFriends: MutableLiveData<HashMap<User, String?>> = MutableLiveData()

    var seenListener: ValueEventListener? = null

    var userDataResponse: MutableLiveData<NetworkingResponse> = MutableLiveData()

    private var imageUploadResponse: MutableLiveData<NetworkingResponse> = MutableLiveData()

    private var resetPasswordResponse: MutableLiveData<NetworkingResponse> = MutableLiveData()

    private var notify: Boolean = false

    companion object {
        const val IMAGE_UPLOAD_SUCCESS = 11
        const val IMAGE_UPLOAD_FAILED = 10
        const val USER_DATA_SUCCESS = 21
        const val USER_DATA_FAILED = 20
        const val RESET_PASSWORD_SUCCESS = 31
        const val RESET_PASSWORD_FAILED = 30
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

    fun register(username: String, name: String, email: String, password: String) =
        Completable.create { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (!emitter.isDisposed) {
                    if (it.isSuccessful) {
                        val map: HashMap<String, String> = hashMapOf(
                            "username" to username,
                            "name" to name,
                            "imageUrl" to "default",
                            "status" to "offline"
                        )
                        currUserReference()?.setValue(map)?.addOnCompleteListener { task ->
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

    fun setUserStatus(status: String) {
        val map: HashMap<String, String> = hashMapOf("status" to status)

        currUserReference()?.updateChildren(map as Map<String, Any>)
    }

    private fun userReference(): DatabaseReference? = firebaseDatabase.getReference("Users")

    private fun chatReference(): DatabaseReference? = firebaseDatabase.getReference("Chats")

    private fun tokenReference(): DatabaseReference? = firebaseDatabase.getReference("Tokens")

    private fun storageReference(): StorageReference? {
        return firebaseStorage.getReference("uploads")
    }

    fun uploadImage(filePath: Uri, fileExtension: String) {
        val ref = storageReference()?.child(
            "uploads/" +
                    System.currentTimeMillis().toString() + "." + fileExtension
        )
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
        }?.addOnFailureListener { e ->
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

    fun getUserData(): Disposable = RxFirebaseDatabase.observeSingleValueEvent(
        currUserReference() as Query,
        User::class.java
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ data ->
            ChatApplication.setUser(data)
            val response = NetworkingResponse(status = USER_DATA_SUCCESS)
            userDataResponse.postValue(response)
        }, {
            val response = NetworkingResponse(USER_DATA_FAILED, it.message, "Failed")
            userDataResponse.postValue(response)
        })

    fun getUserDataAsync(): MutableLiveData<User> {
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

    fun setUserDataResponse(response: NetworkingResponse) {
        userDataResponse.value = response
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
                val friendList: ArrayList<User> = ArrayList()
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

    fun resetPassword(email: String) {
        var response = NetworkingResponse()
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                response.status = RESET_PASSWORD_SUCCESS
                response.title = "Success!"
                response.message = "Please check your email!"
            } else {
                response.status = RESET_PASSWORD_FAILED
                response.title = "Failed!"
                response.message = task.exception?.message
            }
            resetPasswordResponse.postValue(response)
        }
    }

    fun getResetPasswordResponse(): MutableLiveData<NetworkingResponse> {
        return resetPasswordResponse
    }

    fun sendMessage(sender: String, receiver: String, message: String) {
        val map: HashMap<String, Any> = hashMapOf(
            "sender" to sender, "receiver" to receiver,
            "message" to message, "isseen" to false
        )

        chatReference()?.push()?.setValue(map)

        if (notify) {
            sendNotification(receiver, sender, message)
        }
        setNotify(false)
    }

    fun readMessage(myUsername: String, friendUsername: String): MutableLiveData<ArrayList<Chat>> {

        chatReference()?.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatList: ArrayList<Chat> = ArrayList()
                for (snapshot in dataSnapshot.children) {
                    val chatItem = snapshot.getValue(Chat::class.java)
                    chatItem?.let {
                        if ((it.sender == myUsername && it.receiver == friendUsername)
                            || it.receiver == myUsername && it.sender == friendUsername
                        ) {
                            chatList.add(it)
                        }
                    }
                }
                chats.postValue(chatList)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        return chats
    }

    fun seenMessage(myUsername: String, friendUsername: String) {
        seenListener = chatReference()?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children) {
                    val chatItem = dataSnapshot.getValue(Chat::class.java)
                    if (chatItem?.receiver.equals(myUsername) && chatItem?.sender.equals(
                            friendUsername
                        )
                    ) {
                        val map: HashMap<String, Any> = hashMapOf("isseen" to true)
                        dataSnapshot.ref.updateChildren(map)
                    }
                }
            }

        })
    }

    fun deleteSeenMessage() {
        seenListener?.let {
            chatReference()?.removeEventListener(it)
        }
    }

    fun getChatFriendList(myUsername: String): MutableLiveData<HashMap<User, String?>> {

        var lastMessage: HashMap<String, String> = HashMap()

        chatReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val friendList: ArrayList<String> = ArrayList()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat?.sender.equals(myUsername)) {
                        chat?.receiver?.let {
                            if (!friendList.contains(it)) {
                                friendList.add(it)
                            }
                            lastMessage[chat.receiver] = chat.message
                        }
                    }
                    if (chat?.receiver.equals(myUsername)) {
                        chat?.sender?.let {
                            if (!friendList.contains(it)) {
                                friendList.add(it)
                            }
                            lastMessage[chat.sender] = chat.message
                        }
                    }
                }
                getChatFriends(friendList, lastMessage)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        return chatFriends
    }

    fun getChatFriends(friendList: ArrayList<String>, lastMessage: HashMap<String, String>) {
        userReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatFriendsHashMap: HashMap<User, String?> = HashMap()

                for (snapshot in dataSnapshot.children) {
                    val friend = snapshot.getValue(User::class.java)

                    for (username in friendList) {
                        friend?.let {
                            if (it.username.equals(username)) {
                                chatFriendsHashMap[friend] = lastMessage[username]
                            }
                        }
                    }
                }
                chatFriends.value = chatFriendsHashMap
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun updateToken(token: String) {
        tokenReference()?.child(ChatApplication.getUser().username ?: "")?.setValue(token)
    }

    fun getCurrentToken(): String? {
        var newToken: String? = null
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
//                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                newToken = task.result?.token
            })
        return newToken
    }

    fun sendNotification(receiver: String, username: String, message: String) {
        val query = tokenReference()?.orderByKey()?.equalTo(receiver)
        query?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val token = snapshot.child("token").value as String

                    val data = Data(username, R.mipmap.ic_launcher, message, "", receiver)
                    val sender = Sender(data, token)

                    ApiClient.getService().sendNotification(sender)
                        .enqueue(object : Callback<JSONObject> {
                            override fun onResponse(
                                call: Call<JSONObject>,
                                response: Response<JSONObject>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("notif", "success")
                                }
                            }

                            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }
                        })
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun setNotify(value: Boolean) {
        notify = value
    }
}

data class NetworkingResponse(
    var status: Int = 0,
    var message: String? = null,
    var title: String? = null
)
