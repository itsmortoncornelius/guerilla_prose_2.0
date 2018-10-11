package de.handler.mobile.guerillaprose

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.loadUrl(picasso: Picasso, url: String?) {
    if (!url.isNullOrBlank()) picasso.load(url).into(this)
}