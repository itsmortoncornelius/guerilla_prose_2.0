package de.handler.mobile.guerillaprose.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext


class GuerillaProseRepository(private val provider: GuerillaProseProvider): CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val guerillaProsesLiveData = MutableLiveData<List<GuerillaProse?>>()

    fun getGuerillaProses(): LiveData<List<GuerillaProse?>> {
        launch {
            val guerillaProses = provider.getGuerillaProses().await()
            guerillaProsesLiveData.postValue(guerillaProses)
        }

        return guerillaProsesLiveData
    }
}