package de.handler.mobile.guerillaprose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch


class GuerillaProseRepository(private val provider: GuerillaProseProvider) {
    private val guerillaProsesLiveData = MutableLiveData<List<GuerillaProse?>>()

    fun getGuerillaProses(): LiveData<List<GuerillaProse?>> {
        GlobalScope.launch {
            val guerillaProses = provider.getGuerillaProses().await()
            guerillaProsesLiveData.postValue(guerillaProses)
        }

        return guerillaProsesLiveData
    }
}