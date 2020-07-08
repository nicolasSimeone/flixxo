package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.hideKeyboard
import com.flixxo.apps.flixxoapp.utils.onChange
import com.flixxo.apps.flixxoapp.viewModel.SearchViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.search_fragment.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment(), OnItemSelected {

    private lateinit var adapterSearch: MediaAdapterSearch
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle
    private var categoryName = ""
    private var type = 0
    private var selectedContent: String = ""


    companion object {
        fun newInstance() = SearchFragment()
        const val seriesContent = "0"
        const val communityContent = "1"
        const val CONTENT_TYPE_SERIES = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)

        val search = view.findViewById<EditText>(R.id.search)
        val cancelSearch = view.findViewById<TextView>(R.id.clean_search)

        val customProgressView = CustomProgressView(context!!)

        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

        firebaseAnalytics.logEvent("search_screen", bundle)

        arguments?.let {
            val searchText = it.getString("SEARCH_TEXT")
            search.setText(searchText)
            cancelSearch.visibility = if (searchText.isNullOrEmpty()) View.GONE else View.VISIBLE
            searchText?.let { viewModel.search(it) }
        }

        search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                customProgressView.showLoadingDialog()
                viewModel.search(search.text.toString())

                hideKeyboard()
            }
            return@setOnKeyListener false
        }

        search.onChange {
            cancelSearch.visibility = if (search.text.count() == 0) View.GONE else View.VISIBLE
        }


        cancelSearch.setOnClickListener {
            search.text.clear()
            viewModel.clearList()
            cancelSearch.visibility = View.GONE

            view.before_search.visibility = View.VISIBLE
            view.not_found_search.visibility = View.GONE

            hideKeyboard()
        }

        adapterSearch = MediaAdapterSearch(this)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_search)
        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapterSearch

        viewModel.content.observe(this, Observer { value ->
            customProgressView.hideLoadingDialog()

            value?.let {
                adapterSearch.refreshList(it)

                view.before_search.visibility = View.GONE
                view.not_found_search.visibility = if (value.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        return view
    }

    override fun onMediaSelected(uuid: String, contentType: Int) {
        if (contentType == CONTENT_TYPE_SERIES) selectedContent = seriesContent else selectedContent = communityContent
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("UUID_INTENT", uuid)
        intent.putExtra("CONTENT_TYPE_INTENT", contentType)
        intent.putExtra(DetailActivity.SELECTED_CONTENT, selectedContent)
        intent.putExtra("SCREEN", "Search")
        startActivity(intent)
        firebaseAnalytics.logEvent(
            "open_content_detail", bundleOf(
                "UUID" to uuid,
                "Content_type" to contentType
            )
        )
    }

}