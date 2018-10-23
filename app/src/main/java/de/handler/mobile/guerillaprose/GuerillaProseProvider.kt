package de.handler.mobile.guerillaprose

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import okhttp3.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*


class GuerillaProseProvider {
    private val client = OkHttpClient().newBuilder().build()
    private val moshi =
            Moshi.Builder()
                    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
                    .build()

    fun getGuerillaProses(): Deferred<List<GuerillaProse>?> {
        return GlobalScope.async {
            try {
                val request = Request.Builder().url("http://10.0.2.2:8080/guerillaProse").get().build()
                val response = client.newCall(request).execute()
                return@async response.parseItem<List<GuerillaProse>>(moshi, Types.newParameterizedType(List::class.java, GuerillaProse::class.java))
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }

    fun createGuerillaProse(guerillaProse: GuerillaProse): Deferred<GuerillaProse?> {
        return GlobalScope.async {
            try {
                val jsonString = moshi.adapter<GuerillaProse>(
                        Types.newParameterizedType(GuerillaProse::class.java)).toJson(guerillaProse)
                Timber.i("JSON String $jsonString")
                val requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        jsonString)
                val request = Request.Builder().url("http://10.0.2.2:8080/guerillaProse").post(requestBody).build()
                val response = client.newCall(request).execute()
                return@async response.parseItem<GuerillaProse>(moshi, Types.newParameterizedType(GuerillaProse::class.java))
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }



    /**
     * Extension that reads the BufferedSource response
     * of the request and parses the assigned type.
     *
     * Derived & inspired from:
     * https://github.com/ppamorim/okoshi/blob/master/okoshi/src/main/kotlin/com/okoshi/Okoshi.kt
     */
    private fun <T> Response.parseItem(
            moshi: Moshi,
            type: Type): T? {
        return if (isSuccessful) {
            body()?.let { moshi.adapter<T>(type).fromJson(it.source()) }
        } else {
            Timber.e("error while parsing json with code ${code()}")
            null
        }
    }
}
