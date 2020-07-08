package com.flixxo.apps.flixxoapp.view

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Season
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_header.*

class ExpandableHeaderItem(private val season: Season, context: Context) : Item(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup
    private val context:Context = context

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val numberS = season.number
        viewHolder.season.text = String.format(context.getString(R.string.seasonTitle), numberS.toString())
        viewHolder.card.setBackgroundResource(R.drawable.corner_radius_series_expansable)

        viewHolder.item_expandable_header_icon.setImageResource(getRotatedIconId())
        viewHolder.card.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.card.setBackgroundResource(getCornerRadius())
            viewHolder.item_expandable_header_icon.setImageResource(getRotatedIconId())
        }
    }

    override fun getLayout(): Int = R.layout.item_header

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getRotatedIconId() =
        if (expandableGroup.isExpanded) {
            R.drawable.ic_keyboard_arrow_up_black_24dp
        } else {
            R.drawable.ic_expand_more_black_24dp
        }

    private fun getCornerRadius() =
        if (expandableGroup.isExpanded) {
            R.drawable.corner_radius_series_expansable
        } else {
            R.drawable.corner_radius_series_collapse
        }
}