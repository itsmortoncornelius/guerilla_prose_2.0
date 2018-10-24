package de.handler.mobile.guerillaprose.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext


class FlickrRepository(private val provider: FlickrProvider) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val flickrUrlLiveData = MutableLiveData<List<FlickrInfo>>()

    fun getRandomFlickrImages(tag: String): LiveData<List<FlickrInfo>> {
        launch {
            val flickrImages = provider.getRandomImage(tag).await()

            val flickrInfos = mutableListOf<FlickrInfo>()
            flickrImages?.forEach { flickrFoto ->
                if (flickrFoto.id?.isNotBlank() == true
                        && flickrFoto.secret?.isNotBlank() == true
                        && flickrFoto.url_o?.isNotBlank() == true) {

                    flickrInfos.add(FlickrInfo(flickrFoto.url_o, flickrFoto.title, flickrFoto.owner))
                }
            }
            flickrUrlLiveData.postValue(flickrInfos)
        }

        return flickrUrlLiveData
    }
}