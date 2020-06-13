package kv.kvchat.data.network

import com.google.gson.JsonObject
import kv.kvchat.BuildConfig
import kv.kvchat.data.model.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers(
        "Authorization: key=" + BuildConfig.FCM_SERVER_KEY,
        "Content-Type: application/json"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body: Sender): Call<JsonObject>
}