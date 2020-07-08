package com.flixxo.apps.flixxoapp.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.utils.inflate
import com.flixxo.apps.flixxoapp.utils.loadFrom
import kotlinx.android.synthetic.main.my_custom_media_search.view.*

class MediaAdapterSearch(private val onItemSelected: OnItemSelected, private var media: List<Content> = ArrayList()) :
    RecyclerView.Adapter<MediaAdapterSearch.MediaHolder>() {
    override fun onBindViewHolder(holder: MediaHolder, position: Int) {
        val media = media[position]
        holder.bind(media)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaHolder {
        val inflatedView = parent.inflate(R.layout.my_custom_media_search)
        return MediaHolder(inflatedView, onItemSelected)
    }

    override fun getItemCount(): Int = media.count()

    fun refreshList(media: List<Content>) {
        this.media = media
        notifyDataSetChanged()
    }

    class MediaHolder(private val view: View, private val onItemSelected: OnItemSelected) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var media: Content

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemSelected.onMediaSelected(media.uuid!!, media.contentType!!)
        }

        fun bind(media: Content) {
            this.media = media
            if (media.contentType == 2) {
                view.media_image.loadFrom(media.getThumbImageSearch())
            } else {
                view.media_image.loadFrom(media.getThumbImageContent())
            }
        }
    }
}