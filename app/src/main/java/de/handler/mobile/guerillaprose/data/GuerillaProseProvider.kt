package de.handler.mobile.guerillaprose.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.handler.mobile.guerillaprose.BuildConfig
import de.handler.mobile.guerillaprose.parseItem
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class GuerillaProseProvider(val client: OkHttpClient, val moshi: Moshi) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun getGuerillaProses(): Deferred<List<GuerillaProse>?> {
        return async {
            try {
                val request = Request.Builder().url("${BuildConfig.BACKEND_URI}guerillaProse").get().build()
                val response = client.newCall(request).execute()
                return@async response.parseItem<List<GuerillaProse>>(moshi, Types.newParameterizedType(List::class.java, GuerillaProse::class.java))
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }

    fun createGuerillaProse(guerillaProse: GuerillaProse): Deferred<GuerillaProse?> {
        return async {
            try {
                val jsonString = moshi.adapter<GuerillaProse>(
                        Types.newParameterizedType(GuerillaProse::class.java)).toJson(guerillaProse)
                Timber.i("JSON String $jsonString")
                val requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        jsonString)
                val request = Request.Builder().url("${BuildConfig.BACKEND_URI}guerillaProse").post(requestBody).build()
                val response = client.newCall(request).execute()
                return@async response.parseItem<GuerillaProse>(moshi, Types.newParameterizedType(GuerillaProse::class.java))
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }
}
