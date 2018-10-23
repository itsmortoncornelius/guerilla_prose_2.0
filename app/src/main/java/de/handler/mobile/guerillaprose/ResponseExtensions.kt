package de.handler.mobile.guerillaprose

import com.squareup.moshi.Moshi
import okhttp3.Response
import timber.log.Timber
import java.lang.reflect.Type

/**
 * Extension that reads the BufferedSource response
 * of the request and parses the assigned type.
 *
 * Derived & inspired from:
 * https://github.com/ppamorim/okoshi/blob/master/okoshi/src/main/kotlin/com/okoshi/Okoshi.kt
 */
fun <T> Response.parseItem(
        moshi: Moshi,
        type: Type): T? {
    return if (isSuccessful) {
        body()?.let { moshi.adapter<T>(type).fromJson(it.source()) }
    } else {
        Timber.e("error while parsing json with code ${code()}")
        null
    }
}