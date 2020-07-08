package com.flixxo.apps.flixxoapp.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.utils.inflate
import com.flixxo.apps.flixxoapp.utils.loadFrom
import kotlinx.android.synthetic.main.custom_videos_profile.view.*

class VideosAdapter(private val onItemSelected: OnItemSelected, private var video: List<Content> = ArrayList()) :
    RecyclerView.Adapter<VideosAdapter.VideosHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosHolder {
        val inflatedView = parent.inflate(R.layout.custom_videos_profile)
        return VideosHolder(inflatedView, onItemSelected)
    }

    override fun getItemCount(): Int = video.count()

    override fun onBindViewHolder(holder: VideosAdapter.VideosHolder, position: Int) {
        val video = video[position]
        holder.bind(video)
    }

    fun refreshList(video: List<Content>) {
        this.video = video
        notifyDataSetChanged()
    }


    class VideosHolder(private val view: View, private val onItemSelected: OnItemSelected) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var video: Content

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemSelected.onMediaSelected(video.uuid!!, video.contentType!!)
        }

        fun bind(video: Content) {
            this.video = video
            if (video.contentType == 2) view.media_image_video.loadFrom(video.getThumbImageSearch()!!) else view.media_image_video.loadFrom(
                video.getThumbImageContent()!!
            )
        }
    }
}
