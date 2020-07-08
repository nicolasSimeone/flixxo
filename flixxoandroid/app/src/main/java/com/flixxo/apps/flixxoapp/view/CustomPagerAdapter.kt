package com.flixxo.apps.flixxoapp.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.xwray.groupie.ExpandableGroup


class CustomPagerAdapter(fm: FragmentManager, private val list: ArrayList<Pair<String, Fragment>>) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return list[position].second
    }

    override fun getCount(): Int = list.count()

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position].first
    }

    fun refreshEpisodes(groups: ArrayList<ExpandableGroup>) {
        val item = list.first { it.second is DetailEpisodesFragment }.second
        (item as DetailEpisodesFragment).updateView(groups)
    }
}
