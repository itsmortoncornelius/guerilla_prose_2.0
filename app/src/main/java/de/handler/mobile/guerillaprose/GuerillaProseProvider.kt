package de.handler.mobile.guerillaprose

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import okhttp3.*
import timber.log.Timber
import java.lang.reflect.Type


class GuerillaProseProvider {
    private val client = OkHttpClient().newBuilder().build()
    private val moshi = Moshi.Builder().build()

    fun getGuerillaProses(): Deferred<List<GuerillaProse>?> {
        return GlobalScope.async {
            val json = MediaType.parse("application/json; charset=utf-8")
            val body = RequestBody.create(json,
                    "{" +
                            "guerillaproses" +
                            "}")
            val request = Request.Builder().url("http://0.0.0.0:8080/").post(body).build()
            val response = client.newCall(request).execute()
            return@async response.parseItem<List<GuerillaProse>>(moshi, Types.newParameterizedType(List::class.java, GuerillaProse::class.java))
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
