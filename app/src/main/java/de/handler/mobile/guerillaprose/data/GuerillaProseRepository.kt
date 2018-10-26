package de.handler.mobile.guerillaprose.data

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.*
import java.io.File
import kotlin.coroutines.experimental.CoroutineContext


class GuerillaProseRepository(private val guerillaProseProvider: GuerillaProseProvider,
                              private val fileProvider: GuerillaFileProvider): CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val guerillaProsesLiveData = MutableLiveData<List<GuerillaProse?>>()

    fun getGuerillaProses(): LiveData<List<GuerillaProse?>> {
        launch {
            val guerillaProses = guerillaProseProvider.getGuerillaProses().await().orEmpty()
            guerillaProsesLiveData.postValue(guerillaProses)
        }

        return guerillaProsesLiveData
    }

    fun createGuerillaProse(guerillaProse: GuerillaProse): Deferred<GuerillaProse?> {
        return async {
            val createdGuerillaProse = guerillaProseProvider.createGuerillaProse(guerillaProse).await()
            guerillaProseProvider.getGuerillaProses().await().orEmpty()
            return@async createdGuerillaProse
        }
    }

    fun uploadImage(file: File): Deferred<FileInfo?> {
        return fileProvider.uploadFile(file)
    }

    fun uploadImage(bitmap: Bitmap, name: String): Deferred<FileInfo?> {
        return fileProvider.uploadFile(bitmap, name)
    }
}