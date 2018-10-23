package de.handler.mobile.guerillaprose.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import de.handler.mobile.guerillaprose.parseItem
import kotlinx.coroutines.experimental.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import timber.log.Timber
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext


class UserProvider : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val client = OkHttpClient().newBuilder().build()
    private val moshi =
            Moshi.Builder()
                    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
                    .build()

    fun getUser(id: String): Deferred<User?> {
        return async {
            try {
                val request = Request.Builder().url("http://10.0.2.2:8080/user?id=$id").get().build()
                val response = client.newCall(request).execute()
                return@async response.parseItem<User>(moshi, Types.newParameterizedType(User::class.java))
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }

    fun createUser(user: User): Deferred<User?> {
        return async {
            try {
                val jsonString = moshi.adapter<User>(
                        Types.newParameterizedType(User::class.java)).toJson(user)
                Timber.i("JSON String $jsonString")
                val requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        jsonString)
                val request = Request.Builder().url("http://10.0.2.2:8080/user").post(requestBody).build()
                val response = client.newCall(request).execute()
                return@async response.parseItem<User>(moshi, Types.newParameterizedType(User::class.java))
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }
}
