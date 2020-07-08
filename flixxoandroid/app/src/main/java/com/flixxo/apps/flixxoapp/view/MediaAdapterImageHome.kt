package com.flixxo.apps.flixxoapp.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.utils.inflate
import kotlinx.android.synthetic.main.item_image_view.view.*

class MediaAdapterImageHome(private val onItemSelected: OnItemSelectedHome, private var media: List<Content>) :
    RecyclerView.Adapter<MediaAdapterImageHome.MediaHolder>() {

    override fun onBindViewHolder(holder: MediaHolder, position: Int) {
        val media = media[position]
        holder.bind(media)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaHolder {
        val inflatedView = parent.inflate(R.layout.item_image_view)
        return MediaHolder(inflatedView, onItemSelected)
    }

    override fun getItemCount(): Int = media.count()

    class MediaHolder(private val view: View, private val onItemSelected: OnItemSelectedHome) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var content: Content

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemSelected.onMediaSelectedHome(content.uuid!!, content.contentType!!)
        }

        fun bind(content: Content) {
            this.content = content

            view.shimmer_view_container_image.startShimmer()
            view.shimmer_view_container_image.visibility = View.VISIBLE
            view.image_card.visibility = View.GONE

            content.media?.let {
                if (it.isEmpty()) return
                view.image_card.visibility = View.VISIBLE

                content.getThumbImage()?.let {
                    view.media_image_recycler.setImageURI(it)
                }

                view.shimmer_view_container_image.visibility = View.GONE
                view.shimmer_view_container_image.stopShimmer()

            }
        }
    }
}