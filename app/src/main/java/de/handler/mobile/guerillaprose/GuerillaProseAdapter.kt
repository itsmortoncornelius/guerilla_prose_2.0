package de.handler.mobile.guerillaprose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class GuerillaProseAdapter(private val picasso: Picasso) : ListAdapter<GuerillaProse, GuerillaProseAdapter.GuerillaProseViewHolder>(DiffItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuerillaProseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guerilla_prose, parent, false)
        return GuerillaProseViewHolder(picasso, view)
    }

    override fun onBindViewHolder(holder: GuerillaProseViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class GuerillaProseViewHolder(private val picasso: Picasso, itemView: View): RecyclerView.ViewHolder(itemView) {
        private val guerillaImage = itemView.findViewById<ImageView>(R.id.guerilla_image)
        private val guerillaText = itemView.findViewById<TextView>(R.id.guerilla_text)

        fun bind(guerillaProse: GuerillaProse?) {
            guerillaImage.loadUrl(picasso, guerillaProse?.imageUrl)
            guerillaText.text = guerillaProse?.text
        }
    }

    class DiffItemCallback: DiffUtil.ItemCallback<GuerillaProse>() {
        override fun areItemsTheSame(oldItem: GuerillaProse, newItem: GuerillaProse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GuerillaProse, newItem: GuerillaProse): Boolean {
            return oldItem == newItem
        }
    }
}
