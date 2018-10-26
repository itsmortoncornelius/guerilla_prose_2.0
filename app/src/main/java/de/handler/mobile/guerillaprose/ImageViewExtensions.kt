package de.handler.mobile.guerillaprose

import android.widget.ImageView
import com.squareup.picasso.Picasso
import timber.log.Timber

fun ImageView.loadUrl(picasso: Picasso, url: String?, width: Int? = null, height: Int? = null) {
    Timber.i("ImageView url is $url")
    if (!url.isNullOrBlank())
        picasso.load(url)
                .resize(width ?: this.measuredWidth, height ?: this.measuredHeight)
                .into(this)
}