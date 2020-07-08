package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class DetailEpisodesFragment : Fragment(), OnItemSelected {

    private lateinit var seriesAdapter: SeriesAdapter
    private var groupAdapter: GroupAdapter<ViewHolder> = GroupAdapter()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle
    private var categoryName = ""

    companion object {
        private const val EPISODES_PARAM = "EPISODES_PARAM"

        fun newInstance(episodes: ArrayList<ExpandableGroup>): DetailEpisodesFragment {
            val fragment = DetailEpisodesFragment()
            val args = Bundle()
            args.putSerializable(EPISODES_PARAM, episodes)
            fragment.arguments = args
            return fragment
        }
    }

    fun updateView(list: ArrayList<ExpandableGroup>) {
        groupAdapter.addAll(list)
        groupAdapter.notifyDataSetChanged()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.list_episodes_recycler, container, false)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)


        groupAdapter = GroupAdapter<ViewHolder>().apply {
            spanCount = 3

            arguments?.let {
                val list = it.getSerializable("Episodes") as ArrayList<ExpandableGroup>
                addAll(list)
            }

        }

        seriesAdapter = SeriesAdapter(this)
        val seriesList = view.findViewById<RecyclerView>(R.id.seriesList)

        seriesList.apply {
            layoutManager = GridLayoutManager(context, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }


        return view
    }

    override fun onMediaSelected(uuid: String, contentType: Int) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("UUID_INTENT", uuid)
        intent.putExtra("CONTENT_TYPE_INTENT", contentType)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        firebaseAnalytics.logEvent(
            "open_content_detail", bundleOf(
                "UUID" to uuid,
                "Content_type" to contentType
            )
        )
    }

}
