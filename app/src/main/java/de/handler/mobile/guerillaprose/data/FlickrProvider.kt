package de.handler.mobile.guerillaprose.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.handler.mobile.guerillaprose.parseItem
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class FlickrProvider(val client: OkHttpClient, val moshi: Moshi) : CoroutineScope {
    private val baseUrl =
            "https://api.flickr.com/services/rest/" +
                    "?api_key=6ad15c797585b5eb0ca266f9e9cb73ac"
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun getRandomImage(tag: String): Deferred<List<FlickrPhoto>?> {
        return async {
            try {
                val request = Request.Builder().url(baseUrl +
                        "&method=flickr.photos.search" +
                        "&format=json" +
                        "&nojsoncallback=1" +
                        "&text=$tag" +
                        "&per_page=10" +
                        "&extras=url_o").get().build()
                val response = client.newCall(request).execute()
                val flickrWrapper = response.parseItem<FlickrWrapper>(moshi, Types.newParameterizedType(FlickrWrapper::class.java))
                val images = flickrWrapper?.photos?.photo?.toMutableList()
                images?.removeAll { it.url_o.isNullOrEmpty() }
                return@async images
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }
}
