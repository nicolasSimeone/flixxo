package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.model.ContentCategory
import com.flixxo.apps.flixxoapp.utils.loadFrom
import com.flixxo.apps.flixxoapp.viewModel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ViewListener
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(), OnItemSelectedHome {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var carouselView: CarouselView
    private lateinit var adapterHome: MediaAdapterHome
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var layoutCarouselContainer: RelativeLayout
    private lateinit var tabsNav: TabLayout
    private var categoryName: String = ""
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle
    lateinit var content: Content

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        progressBarLoading = view.findViewById(R.id.progress_bar_loading)
        layoutCarouselContainer = view.findViewById(R.id.layout_carousel_container)
        carouselView = view.findViewById(R.id.customCarouselView)
        tabsNav = view.findViewById(R.id.tabsNav)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

        firebaseAnalytics.logEvent("home_screen", bundle)

        adapterHome = MediaAdapterHome(context!!, this)

        view.findViewById<RecyclerView>(R.id.recycler_view_media).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterHome
        }

        setViewModelObservers()
        setTabs()

        viewModel.topContents.observe(this, Observer { value ->
            if (value.isEmpty()) return@Observer

            value?.let {

                val viewListener = ViewListener { position ->
                    val item: View = layoutInflater.inflate(R.layout.items_carousel, null)

                    it[position].getMainImage()?.let { url ->
                        item.findViewById<ImageView>(R.id.imageCarousel).loadFrom(url)
                    }

                    val carouselVisibility = if (it.size > 1) View.VISIBLE else View.GONE
                    carouselView.setIndicatorVisibility(carouselVisibility)

                    return@ViewListener item
                }

                carouselView.setViewListener(viewListener)
                carouselView.pageCount = it.size

                carouselView.setImageClickListener { position ->
                    firebaseAnalytics.logEvent(
                        "open_top_content_detail", bundleOf(
                            "UUID" to it[position].uuid,
                            "Content_type" to it[position].contentType,
                            "Category" to categoryName
                        )
                    )


                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("UUID_INTENT", it[position].uuid)
                    intent.putExtra("CONTENT_TYPE_INTENT", it[position].contentType)
                    intent.putExtra(DetailActivity.SELECTED_CONTENT, tabsNav.selectedTabPosition.toString())
                    intent.putExtra("SCREEN", "Home")
                    startActivity(intent)
                }

                hideLoadingCarousel()
            }
        })

        viewModel.seriesTop.observe(this, Observer { value ->
            if (value.isEmpty()) return@Observer

            value?.let {
                val viewListener = ViewListener { position ->
                    val item: View = layoutInflater.inflate(R.layout.items_carousel, null)

                    it[position].getMainImage()?.let { url ->
                        item.findViewById<ImageView>(R.id.imageCarousel).loadFrom(url)
                    }

                    return@ViewListener item
                }

                carouselView.setViewListener(viewListener)
                carouselView.pageCount = it.size

                val carouselVisibility = if (it.size > 1) View.VISIBLE else View.GONE
                carouselView.setIndicatorVisibility(carouselVisibility)

                carouselView.setImageClickListener { position ->
                    firebaseAnalytics.logEvent(
                        "open_top_content_detail", bundleOf(
                            "UUID" to it[position].uuid,
                            "Content_type" to it[position].convertToContent().contentType,
                            "Category" to categoryName
                        )
                    )


                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("UUID_INTENT", it[position].convertToContent().uuid)
                    intent.putExtra("CONTENT_TYPE_INTENT", it[position].convertToContent().contentType)
                    intent.putExtra("SCREEN", "Home")
                    intent.putExtra(DetailActivity.SELECTED_CONTENT, tabsNav.selectedTabPosition.toString())
                    startActivity(intent)
                }

                hideLoadingCarousel()
            }
        })

        viewModel.getTopSeries()
        viewModel.loadContentByCategories(HomeViewModel.HomeContentType.Series)

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAd()
        viewModel.updateContentDb()
    }

    override fun onMediaSelectedHome(uuid: String, contentType: Int) {
        uuid.let { if (it.isEmpty()) return }

        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("UUID_INTENT", uuid)
        intent.putExtra("CONTENT_TYPE_INTENT", contentType)
        intent.putExtra(DetailActivity.SELECTED_CONTENT, tabsNav.selectedTabPosition.toString())
        intent.putExtra("SCREEN", "Home")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        firebaseAnalytics.logEvent(
            "open_content_detail", bundleOf(
                "UUID" to uuid,
                "Content_type" to contentType
            )
        )
    }

    private val contentCategory = mutableListOf<ContentCategory>()

    private fun setViewModelObservers() {
        viewModel.categoryContents.observe(viewLifecycleOwner, Observer {
            val contentCategoryWrapper = getContentCategoryWrapperList(it.toMutableList())
            adapterHome.refreshList(contentCategoryWrapper)
            viewModel.getAd()
        })

        viewModel.adReward.observe(viewLifecycleOwner, Observer {
            adapterHome.refreshPriceAds(it)
        })
    }

    private fun setTabs() {
        tabsNav.isTabIndicatorFullWidth = true
        tabsNav.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Clean category-contents list
                adapterHome.refreshList(listOf())

                // Clean carousel
                carouselView.containerViewPager.removeAllViews()
                carouselView.setIndicatorVisibility(View.GONE)

                val type = when (tab?.position) {
                    0 -> HomeViewModel.HomeContentType.Series
                    1 -> HomeViewModel.HomeContentType.Community
                    else -> HomeViewModel.HomeContentType.Community
                }
                if (tab?.position == 0) firebaseAnalytics.logEvent(
                    "series_tab_selected",
                    bundle
                ) else firebaseAnalytics.logEvent("community_tab_selected", bundle)


                viewModel.getTopContent(type)
                viewModel.loadContentByCategories(type)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun hideLoadingCarousel() {
        progressBarLoading.visibility = View.GONE
        layoutCarouselContainer.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun getContentCategoryWrapperList(listMediaContents: MutableList<ContentCategory>): List<ContentCategoryWrapper> {
        val completeList = mutableListOf<ContentCategoryWrapper>()

        while (listMediaContents.count() > 0) {
            val count = if (listMediaContents.count() >= 5) 5 else listMediaContents.count()
            for (item in listMediaContents.take(count)) {
                completeList.add(ContentCategoryWrapper(item, HomeMediaType.MEDIA))
                listMediaContents.remove(item)
            }

            if (count == 5 && listMediaContents.count() >= 0) {
                completeList.add(ContentCategoryWrapper(null, HomeMediaType.AD))
            }
        }
        return completeList.toList()
    }


}
