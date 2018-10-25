package de.handler.mobile.guerillaprose

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.loadUrl(picasso: Picasso, url: String?, width: Int? = null, height: Int? = null) {
    if (!url.isNullOrBlank())
        picasso.load(url)
                .resize(width ?: this.measuredWidth, height ?: this.measuredHeight)
                .into(this)
}