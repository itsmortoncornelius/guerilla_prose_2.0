package de.handler.mobile.guerillaprose.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.handler.mobile.guerillaprose.parseItem
import kotlinx.coroutines.experimental.*
import okhttp3.*
import timber.log.Timber
import java.io.File
import kotlin.coroutines.experimental.CoroutineContext


class GuerillaFileProvider(private val client: OkHttpClient, val moshi: Moshi) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun uploadFile(file: File): Deferred<FileInfo?> {
        return async {
            try {
                val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(file.name, file.name, RequestBody.create(MediaType.parse("image/jpg"), file))
                        .build()

                val request = Request.Builder()
                        .url("http://10.0.2.2:8080/file")
                        .post(requestBody)
                        .build()

                val response = client.newCall(request).execute()
                return@async response.parseItem<FileInfo>(moshi, Types.newParameterizedType(FileInfo::class.java))
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }
}
