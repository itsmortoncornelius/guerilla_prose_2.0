package de.handler.mobile.guerillaprose.presentation

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.handler.mobile.guerillaprose.BuildConfig
import de.handler.mobile.guerillaprose.R
import de.handler.mobile.guerillaprose.data.GuerillaProse
import de.handler.mobile.guerillaprose.loadUrl


class GuerillaProseAdapter(private val picasso: Picasso) :
    ListAdapter<GuerillaProse, GuerillaProseAdapter.GuerillaProseViewHolder>(DiffItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuerillaProseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guerilla_prose, parent, false)
        return GuerillaProseViewHolder(picasso, view)
    }

    override fun onBindViewHolder(holder: GuerillaProseViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class GuerillaProseViewHolder(private val picasso: Picasso, itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val guerillaImage = itemView.findViewById<ImageView>(R.id.proseImageView)
        private val guerillaText = itemView.findViewById<TextView>(R.id.proseText)
        private val container = itemView.findViewById<CardView>(R.id.container)

        fun bind(guerillaProse: GuerillaProse?) {
            guerillaImage.loadUrl(picasso, "${BuildConfig.BACKEND_URI}${guerillaProse?.imageUrl}", height = 350, onBitmapLoadedAction = {
                val bitmap = (guerillaImage.drawable as? BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    Palette.from(bitmap).generate { palette ->
                        palette?.lightVibrantSwatch?.rgb?.let { color ->
                            container.setCardBackgroundColor(color)
                        }
                        palette?.lightVibrantSwatch?.bodyTextColor?.let { color ->
                            guerillaText.setTextColor(color)
                        }
                    }
                }
            })

            guerillaText.text = guerillaProse?.text
        }
    }

    class DiffItemCallback : DiffUtil.ItemCallback<GuerillaProse>() {
        override fun areItemsTheSame(oldItem: GuerillaProse, newItem: GuerillaProse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GuerillaProse, newItem: GuerillaProse): Boolean {
            return oldItem == newItem
        }
    }
}
