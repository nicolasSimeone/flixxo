package com.flixxo.apps.flixxoapp.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.model.Season
import com.flixxo.apps.flixxoapp.utils.formatValue
import com.flixxo.apps.flixxoapp.utils.inflate
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_row.view.*

class SeriesAdapter(
    private val onItemSelected: OnItemSelected,
    private var media: List<SerieDetailWrapper> = ArrayList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RowHolder -> {
                holder.bind(media[position].content!!)
            }
            is HeaderHolder -> {
                holder.bind(media[position].season!!)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SerieDetailRow.HEADER.ordinal -> HeaderHolder(parent.inflate(R.layout.item_header))
            SerieDetailRow.ROW.ordinal -> RowHolder(parent.inflate(R.layout.item_row), onItemSelected)
            else -> RowHolder(parent.inflate(R.layout.item_row), onItemSelected)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return media[position].type.ordinal
    }

    override fun getItemCount(): Int = media.count()

    fun refreshList(media: List<SerieDetailWrapper>) {
        this.media = media
        notifyDataSetChanged()
    }

    class RowHolder(private val view: View, private val onItemSelected: OnItemSelected) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private lateinit var media: Content

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemSelected.onMediaSelected(media.uuid!!, media.contentType!!)
        }

        fun bind(content: Content) {
            this.media = content
            val title = content.title
            view.episodes.text = title
            val price: String = content.price!!.formatValue()
            view.detail_episodes.text = price
        }
    }

    class HeaderHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(titleSeason: Season) {
            val title = titleSeason.title
            view.season.text = title
        }
    }
}
