package de.handler.mobile.guerillaprose.data

data class FlickrWrapper(val photos: FlickrPhotos?)
data class FlickrPhotos(val photo: List<FlickrPhoto>)
data class FlickrPhoto(
        val id: String?,
        val owner: String?,
        val secret: String?,
        val server: String?,
        val farm: String?,
        val title: String?,
        val url_o: String?
)
data class FlickrInfo(
        val imageUrl: String,
        val imageTitle: String?,
        val owner: String?,
        val ownerUrl: String? = null
)