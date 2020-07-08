package com.flixxo.apps.flixxoapp.view

import android.view.View
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Content
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_row.*

class EpisodesItem(
    private val content: Content,
    private val callback: OnItemSelected,
    private val order: Int,
    private val isPurchased: Boolean
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val counter = if (order + 1 < 10) "0$order" else "$order"
        val mediaContent = content.title.toString()
        val titleCount = "$counter - $mediaContent"
        viewHolder.episodes.text = titleCount
        viewHolder.detail_episodes.text = content.price.toString()
        viewHolder.episodes_row.setOnClickListener {
            callback.onMediaSelected(content.uuid!!, content.contentType!!)
        }

        if (isPurchased) {
            viewHolder.detail_episodes.visibility = View.GONE
            viewHolder.image_check.visibility = View.VISIBLE
        } else {
            viewHolder.detail_episodes.visibility = View.VISIBLE
            viewHolder.image_check.visibility = View.GONE

        }
    }

    override fun getLayout(): Int = R.layout.item_row

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 1
}