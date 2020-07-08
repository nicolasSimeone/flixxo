package com.flixxo.apps.flixxoapp.view

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.ContentCategory
import com.flixxo.apps.flixxoapp.utils.inflate
import kotlinx.android.synthetic.main.earn_flixx_home.view.*
import kotlinx.android.synthetic.main.my_custom_media_home.view.*

class MediaAdapterHome(
    private val context: Context,
    private val onItemSelected: OnItemSelectedHome,
    private var media: MutableList<ContentCategoryWrapper> = ArrayList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when (holder) {
            is MediaHolder -> {
                val media = media[position]
                holder.bind(media.contentCategory!!)
            }
            is EarnFlixxHolder -> {
                val price =
                    if (payloads.count() > 0) payloads[0] else 0.00
                holder.bind(context.getString(R.string.earn_flixx_ads, price))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemViewType(position: Int): Int {
        return media[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HomeMediaType.AD.ordinal -> EarnFlixxHolder(parent.inflate(R.layout.earn_flixx_home))
            else -> {
                MediaHolder(parent.inflate(R.layout.my_custom_media_home), onItemSelected)
            }
        }
    }

    override fun getItemCount(): Int = media.count()

    fun refreshPriceAds(price: String) {
        media.let {
            media.forEach { item ->
                if (item.type == HomeMediaType.AD) {
                    notifyItemChanged(media.indexOf(item), price)
                }
            }
        }
    }

    fun refreshList(media: List<ContentCategoryWrapper>) {
        this.media.clear()
        this.media = media.toMutableList()
        notifyDataSetChanged()
    }

    class EarnFlixxHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(price: String) {
            view.earn_flixx_button.text = price
            view.earn_flixx_button.setOnClickListener {
                it.context.startActivity(Intent(view.context, AdPlayerActivity::class.java))
            }
        }
    }

    class MediaHolder(private val view: View, private val onItemSelected: OnItemSelectedHome) :
        RecyclerView.ViewHolder(view) {

        fun bind(media: ContentCategory) {

            view.shimmer_container.startShimmer()

            view.shimmer_container.visibility = View.VISIBLE
            view.llRealContent.visibility = View.GONE

            if (media.category.id > 0) {

                view.llRealContent.visibility = View.VISIBLE
                view.category_name.text = media.category.name

                view.shimmer_container.visibility = View.GONE
                view.shimmer_container.stopShimmer()
            }

            val adapterHomeImage = MediaAdapterImageHome(onItemSelected, media.contents)

            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_image)
            recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = adapterHomeImage

        }
    }
}
