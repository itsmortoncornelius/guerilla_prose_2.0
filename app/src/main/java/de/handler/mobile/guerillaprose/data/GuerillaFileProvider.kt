package de.handler.mobile.guerillaprose.data

import android.graphics.Bitmap
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.handler.mobile.guerillaprose.BuildConfig
import de.handler.mobile.guerillaprose.parseItem
import kotlinx.coroutines.*
import okhttp3.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.coroutines.CoroutineContext


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

                return@async uploadFile(requestBody).await()
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }

    fun uploadFile(bitmap: Bitmap, name: String): Deferred<FileInfo?> {
        return async {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val byteArray = stream.toByteArray()

                val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(name, name, RequestBody.create(MediaType.parse("image/jpg"), byteArray))
                        .build()

                return@async uploadFile(requestBody).await()
            } catch (e: Exception) {
                Timber.e(e)
                return@async null
            }
        }
    }

    private fun uploadFile(requestBody: RequestBody): Deferred<FileInfo?> {
        return async {

            val request = Request.Builder()
                    .url("${BuildConfig.BACKEND_URI}file")
                    .post(requestBody)
                    .build()

            val response = client.newCall(request).execute()
            return@async response.parseItem<FileInfo>(moshi, Types.newParameterizedType(FileInfo::class.java))
        }
    }
}
