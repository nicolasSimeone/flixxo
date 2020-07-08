package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.flixxo.apps.flixxoapp.BuildConfig
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.*
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.*
import com.flixxo.apps.flixxoapp.viewModel.DetailViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.masterwok.simplevlcplayer.activities.MediaPlayerActivity
import com.masterwok.simplevlcplayer.fragments.Subtitle
import com.masterwok.simplevlcplayer.fragments.SerieInteractivePlayer
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.my_custom_toolbar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File

class DetailActivity : AppCompatActivity(), SearchClicked, OnItemSelected {

    companion object {
        const val SELECTED_CONTENT: String = "SELECTED_CONTENT"
        const val TAB_INFORMATION: Int = 0
        const val TAB_EPISODES: Int = 1
        const val SERIES_CONTENT_HOME = "0"
        const val COMMUNITY_CONTENT_HOME = "1"
        const val CONTENT_TYPE_SERIES = 2
        const val CONTENT_TYPE_EPISODES = 1
    }

    private val viewModel by viewModel<DetailViewModel>(key = "detail")
    private lateinit var header: CustomToolbarView
    private var playBool: Boolean = false
    private var contentPurchased: List<ContentPurchased> = mutableListOf()
    private var seasons: MutableList<Season> = mutableListOf()
    private var mutableList: MutableList<EpisodesItem> = mutableListOf()
    private var episodesList: ArrayList<ExpandableGroup> = arrayListOf()
    private lateinit var customPagerAdapter: CustomPagerAdapter
    private lateinit var serie: Series
    private lateinit var bundle: Bundle
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var viewPager: ViewPager
    private val preferencesManager: PreferencesManager by inject()
    private var uuid: String = ""
    private var contentType: Int = 0
    private var price: Double = 0.0
    private var subtitles: ArrayList<Subtitle> = arrayListOf()
    private var audioLang: String = ""
    private var media: String = ""
    private var nextUuid: String = ""
    private var nextPrice: Double = 0.0
    private var nextSubtitles: ArrayList<Subtitle> = arrayListOf()
    private var nextAudioLang: String = ""
    private var newTorrentFile: NewTorrentFile = NewTorrentFile("", "", "", "", "", TorrentFile())
    private var isNextEpisode: Boolean = true
    private var backHome: Boolean = false
    private var uuidSerie: String = ""
    private var selectedContentHome: String = ""
    private var tab: Int = 0
    private var screen: String = ""
    private val priceAltEscEpisode: Double = 1.0
    private var startAgain: Boolean = false
    private var userResume: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.onAttach(this)
        setContentView(R.layout.activity_detail)
        checkConnectivity()
        hideControlBar(true)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        hideControlBar(true)
        userResume = preferencesManager.getString("NICKNAME_RESUME")

        firebaseAnalytics.logEvent("detail_screen", bundle)

        pill_next_ep_gone.setBackgroundResource(R.drawable.button_clicked)
        pill_gone.setBackgroundResource(R.drawable.button_clicked)

        val tabLayout = findViewById<TabLayout>(R.id.tabNavDetail)
        viewPager = findViewById(R.id.viewpager)
        earnButton_custom.setOnClickListener {
            startActivity(Intent(this, AdPlayerActivity::class.java))
        }


        viewModel.balance.observe(this, Observer {
            header = findViewById(R.id.toolbar_header_detail)
            header.findViewById<TextView>(R.id.balance_amount_detail).text = it.amount.formatValue()
        })


        val bottomBar = findViewById<BottomNavigationView>(R.id.bottom_nav_detail)
        bottomBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("ITEMID", R.id.home)
                    intent.putExtra("UUID_INTENT", uuid)
                    intent.putExtra("CONTENT_TYPE_INTENT", contentType)
                    startActivity(intent)
                    true
                }
                R.id.gamification -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("ITEMID", R.id.gamification)
                    intent.putExtra("UUID_INTENT", uuid)
                    intent.putExtra("CONTENT_TYPE_INTENT", contentType)
                    startActivity(intent)
                    true
                }
                R.id.search -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("ITEMID", R.id.search)
                    intent.putExtra("UUID_INTENT", uuid)
                    intent.putExtra("CONTENT_TYPE_INTENT", contentType)
                    startActivity(intent)
                    true
                }
                R.id.settings -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("ITEMID", R.id.settings)
                    intent.putExtra("UUID_INTENT", uuid)
                    intent.putExtra("CONTENT_TYPE_INTENT", contentType)
                    startActivity(intent)
                    true
                }
                else -> false
            }

        }

        no_internet_detail.tryAgainAction = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val connectivity = ConnectivityHelper.getConnectionType(this)
                if (connectivity == NetworkCapabilities.TRANSPORT_CELLULAR or NetworkCapabilities.TRANSPORT_WIFI) {
                    no_internet_detail.visibility = View.GONE
                    relative_detail.visibility = View.VISIBLE
                    toolbar_header_detail.visibility = View.VISIBLE
                    recreate()
                }
            } else {
                val connectivity = ConnectivityHelper.getConnectionTypeSDK21(this)
                if (connectivity == 0 or ConnectivityManager.TYPE_WIFI) {
                    no_internet_detail.visibility = View.GONE
                    relative_detail.visibility = View.VISIBLE
                    toolbar_header_detail.visibility = View.VISIBLE
                    recreate()
                }
            }
        }

        val isNotification = intent.extras?.get("IS_NOTIFICATION") ?: false
        selectedContentHome = intent.extras?.getString(SELECTED_CONTENT) ?: COMMUNITY_CONTENT_HOME
        screen = intent.extras?.getString("SCREEN") ?: "Detail"

        tab = intent.extras?.getInt("TAB")!!

        if (isNotification as Boolean) {
            uuid = intent.extras?.get("UUID_NOTIFICATION") as String
            contentType = intent.extras?.get("CONTENT_TYPE_NOTIFICATION") as Int
        } else {
            uuid = intent.extras?.get("UUID_INTENT") as String
            contentType = intent.extras?.get("CONTENT_TYPE_INTENT") as Int
            if (contentType == CONTENT_TYPE_SERIES) {
                uuidSerie = intent.extras?.get("UUID_INTENT") as String
            } else {
                isNextEpisode = false
                if (selectedContentHome == SERIES_CONTENT_HOME) {
                    uuidSerie = intent.extras?.get("UUID_SERIE") as String
                }

            }
        }

        viewModel.content.observe(this, Observer { content ->
            val relativeLayout = findViewById<RelativeLayout>(R.id.relative_detail)
            relativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.flixxoBackgorundColor))

            content.getMainImage()?.let {
                detail_image_video.loadFrom(it)
                media = it
            }

            content.price?.let {
                detail_price.text = it.formatValue()
                price = it
            }

            content.subtitle?.let {
                subtitles = it
            }

            content.audioLang?.let {
                audioLang = it
            }

            viewModel.canUserPlay(uuid, price)
        })

        viewModel.series.observe(this, Observer { series ->
            series.convertToContent().getMainImage()?.let {
                detail_image_video.loadFrom(it)
            }
        })

        customPagerAdapter = CustomPagerAdapter(
            supportFragmentManager, arrayListOf(
                Pair(getString(R.string.information), DetailInformationFragment()),
                Pair(getString(R.string.episodes), DetailEpisodesFragment())
            )
        )
        viewPager.adapter = customPagerAdapter


        tabLayout.setupWithViewPager(viewPager)

        tabLayout.isTabIndicatorFullWidth = true

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position: Int = tab.position
                if (position == 0) firebaseAnalytics.logEvent(
                    "info_tab_selected",
                    bundle
                ) else firebaseAnalytics.logEvent("episodes_tab_selected", bundle)
                viewPager.setCurrentItem(position, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


        if (tab == TAB_EPISODES) {
            viewPager.setCurrentItem(1, true)
        }

        detail_episode.setOnClickListener {
            pill_next_ep_gone.setBackgroundResource(R.drawable.button_clicked)
            pill_gone.setBackgroundResource(R.drawable.button_clicked)
            isNextEpisode
            decideButtonNextEpisodeAction()
        }
        back_button.setOnClickListener {
            if (screen.equals("SelectedContent")) {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("UUID_INTENT", uuidSerie)
                intent.putExtra("CONTENT_TYPE_INTENT", 2)
                intent.putExtra("UUID_SERIE", uuidSerie)
                intent.putExtra(SELECTED_CONTENT, selectedContentHome)
                intent.putExtra("TAB", TAB_EPISODES)
                intent.putExtra("SCREEN", "Detail")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                firebaseAnalytics.logEvent("open_content_detail", bundleOf("UUID" to uuid))
                finish()
            } else {
                onBackPressed()
            }

        }

        if(contentType == 1) {
            tabLayout.visibility = View.GONE
            pill_gone.visibility = View.VISIBLE
            viewPager.beginFakeDrag()
            gradient.setBackgroundResource(R.drawable.gradient_layer)
        } else {
            pill_gone.visibility = View.INVISIBLE
            tabLayout.visibility = View.VISIBLE
        }
        //show screen for interactive serie
        if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) {
            tabLayout.visibility = View.GONE
            viewPager.beginFakeDrag()
            gradient.setBackgroundResource(R.drawable.gradient_layer)
            detail_view.setBackgroundColor(resources.getColor(R.color.flixxoBackgorundColor))
            pill_gone.visibility = View.VISIBLE
            watch_now.text = getString(R.string.play_now)
            flixxo_image_price.visibility = View.GONE
        }


        pill_container.setOnClickListener {
            pill_gone.setBackgroundResource(R.drawable.button_clicked)
            pill_container.setBackgroundResource(R.drawable.button_clicked)
            separate.setBackgroundResource(R.drawable.button_clicked)
            isNextEpisode = false
            decideButtonAction()
            firebaseAnalytics.logEvent("watch_now", bundle)
        }

        pay_to_watch.setOnClickListener {
            pill_gone.setBackgroundResource(R.drawable.button_clicked)
            pill_container.setBackgroundResource(R.drawable.button_clicked)
            playBool = true
            flixxo_image_price.visibility = View.GONE
            watch_now.visibility = View.GONE
            pill_container_item.visibility = View.GONE
            pill_container.visibility = View.GONE
            pill_gone.setBackgroundResource(android.R.color.transparent)
            pill_container.setBackgroundResource(android.R.color.transparent)
            card_tick.visibility = View.VISIBLE
            card_tick.postDelayed({
                cheked_paid.visibility = View.VISIBLE
            }, 2000)

            //pay interactive serie
            if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) viewModel.pay(BuildConfig.INTERACTIVE_SERIE_EPISODE_UUID, priceAltEscEpisode) else viewModel.pay(uuid, price)

            firebaseAnalytics.logEvent("pay_to_watch", bundle)

        }

        pay_to_watch_next_ep.setOnClickListener {
            playBool = true
            detail_episode.visibility = View.GONE
            backHome = false
            next_ep_container.visibility = View.GONE
            pill_next_ep_gone.setBackgroundResource(android.R.color.transparent)
            next_ep_container.setBackgroundResource(android.R.color.transparent)
            card_tick_next_ep.visibility = View.VISIBLE
            card_tick_next_ep.postDelayed({
                check_next_ep.visibility = View.VISIBLE
            }, 2000)

            //pay interactive serie
            if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) viewModel.pay(BuildConfig.INTERACTIVE_SERIE_EPISODE_UUID, priceAltEscEpisode) else viewModel.pay(nextUuid, nextPrice)

            firebaseAnalytics.logEvent("pay_to_watch", bundle)
        }
        start_again.setOnClickListener {
            val alert = AlertDialog.Builder(this, R.style.CustomDialog)
            alert.setTitle(getString(R.string.sure_start_again))
            alert.setPositiveButton(getString(R.string.yes)) { _, _ ->
                startAgain = true
                preferencesManager.clearKey("${BuildConfig.INTERACTIVE_SERIE_EPISODE_UUID}-$userResume")
                startVideoPlayer(File.createTempFile(".flixxo",".tmp"))
            }
            alert.setNegativeButton(R.string.no, null)
            alert.setCancelable(false)
            alert.show()
        }

        viewModel.success.observe(this, Observer {
            if (!it) {
                print(getString(R.string.paymentFailed))
            } else {
                if(uuid != BuildConfig.INTERACTIVE_SERIE_UUID) {
                    val torrentSize = viewModel.content.value?.torrentFile?.totalLength?.toLong() ?: 0
                    this@DetailActivity.startTorrentStreaming(torrentSize)
                }
                else {
                    this@DetailActivity.startVideoPlayer(File.createTempFile(".flixxo", ".tmp"))
                }
            }
        })



        viewModel.status.observe(this, Observer { status ->
            loadWatchButoon(status == DetailViewModel.UserContentStatus.Purchased)
        })


        viewModel.isPurchased.observe(this, Observer {
            loadWatchButoon(it)
        })


        viewModel.series.observe(this, Observer {
            serie = it
        })

        viewModel.contentPurchased.observe(this, Observer {
            this.contentPurchased = it
            //don't show episode list in interactive serie
            if(uuid != BuildConfig.INTERACTIVE_SERIE_UUID) createList()
        })

        viewModel.seasons.observe(this, Observer { seasons ->
            this.seasons.clear()
            this.seasons = seasons.toMutableList()
            viewModel.getContentPurchased()
        })

        viewModel.torrentFile.observe(this, Observer {
            newTorrentFile = it
        })

        viewModel.getTorrentFile(uuid)
        viewModel.loadDetail(uuid, contentType)
        if (uuid == BuildConfig.INTERACTIVE_SERIE_UUID) viewModel.canUserPlay(BuildConfig.INTERACTIVE_SERIE_EPISODE_UUID, priceAltEscEpisode) else viewModel.canUserPlay(uuid, price)
        viewModel.getLanguages(assets)
        viewModel.getFollowings()
    }

    private fun checkConnectivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (ConnectivityHelper.getConnectionType(this)) {
                NetworkCapabilities.TRANSPORT_WIFI -> {
                    ConnectivityHelper.logEvent(NetworkCapabilities.TRANSPORT_WIFI, this)
                    no_internet_detail.visibility = View.GONE
                    relative_detail.visibility = View.VISIBLE
                }
                NetworkCapabilities.TRANSPORT_CELLULAR -> {
                    ConnectivityHelper.logEvent(NetworkCapabilities.TRANSPORT_CELLULAR, this)
                    no_internet_detail.visibility = View.GONE
                    relative_detail.visibility = View.VISIBLE
                }

                else -> {
                    ConnectivityHelper.logEvent(-1, this)
                    no_internet_detail.visibility = View.VISIBLE
                    relative_detail.visibility = View.GONE
                }
            }
        } else {
            when (ConnectivityHelper.getConnectionTypeSDK21(this)) {
                ConnectivityManager.TYPE_WIFI -> {
                    ConnectivityHelper.logEvent(ConnectivityManager.TYPE_WIFI, this)
                    no_internet_detail.visibility = View.GONE
                    relative_detail.visibility = View.VISIBLE
                }
                0 -> {
                    ConnectivityHelper.logEvent(0, this)
                    no_internet_detail.visibility = View.GONE
                    relative_detail.visibility = View.VISIBLE
                }

                else -> {
                    ConnectivityHelper.logEvent(-1, this)
                    no_internet_detail.visibility = View.VISIBLE
                    relative_detail.visibility = View.GONE
                    hideControlBar(true)
                }
            }
        }
    }

    private fun decideButtonAction() {

        when (viewModel.status.value) {
            DetailViewModel.UserContentStatus.Purchased -> {
                if (uuid == BuildConfig.INTERACTIVE_SERIE_UUID) {
                    val directory = FileHelper.getTorrentDirectory(this.applicationContext, BuildConfig.INTERACTIVE_SERIE_UUID)
                    if (directory.exists()) {
                        FileHelper.getNone(this.applicationContext)
                        FileHelper.getMedia(this.applicationContext)
                        startVideoPlayer(File.createTempFile(".flixxo", ".tmp"))
                    }
                } else {

                    val torrentFile = FileHelper.getTorrentDirectory(this.applicationContext, uuid ?: "")
                    val torrentSize = viewModel.content.value?.torrentFile?.totalLength?.toLong() ?: 0;

                    // File exists
                    if (torrentFile.exists()) {
                        FileHelper.getNone(this.applicationContext)
                        FileHelper.getMedia(this.applicationContext)
                        if (torrentFile.listFiles().filter { it.toString() != ".nomedia" }.count() > 0) {
                            val torrentMp4 = torrentFile.listFiles().filter { it.toString() != ".nomedia" }.first()
                            // File is complete
                            if (torrentMp4.length() == torrentSize) {
                                startVideoPlayer(torrentMp4)
                            } else {
                                torrentFile.deleteRecursively()
                                startTorrentStreaming(torrentSize)
                            }
                        } else {
                            startTorrentStreaming(torrentSize)
                        }
                    } else {
                        startTorrentStreaming(torrentSize)
                    }
                }
            }

            DetailViewModel.UserContentStatus.CanPurchase -> {
                watch_now.visibility = View.GONE
                pill_container.setBackgroundResource(R.drawable.button_pay_to_watch)
                separate.setBackgroundResource(R.drawable.button_pay_to_watch)
                pill_gone.setBackgroundResource(R.drawable.button_pay_to_watch)
                val detailPrice = price.formatValue()
                if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) pay_to_watch.text = String.format(getString(R.string.price_altesc), 1.0) else pay_to_watch.text = String.format(getString(R.string.detailPrice), detailPrice)
                pill_container_item.visibility = View.GONE
                pill_container.visibility = View.VISIBLE
                pay_to_watch.visibility = View.VISIBLE

                pill_gone.postDelayed({
                    if (playBool) {
                        return@postDelayed
                    }
                    if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) flixxo_image_price.visibility = View.GONE else flixxo_image_price.visibility = View.VISIBLE
                    watch_now.visibility = View.VISIBLE
                    pill_container_item.visibility = View.VISIBLE
                    pill_gone.setBackgroundResource(R.drawable.pill_flixxo_coin)
                    pill_container.setBackgroundResource(R.drawable.pill_flixxo_coin)
                    separate.setBackgroundResource(android.R.color.white)
                    pill_container.visibility = View.VISIBLE
                    pay_to_watch.visibility = View.GONE

                }, 4000)
            }

            DetailViewModel.UserContentStatus.DontEnoughMoney -> {
                val intent = Intent(this, NoFlixxAdActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun decideButtonNextEpisodeAction() {
        when (viewModel.status.value) {
            DetailViewModel.UserContentStatus.Purchased -> {
                if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) startVideoPlayer(File.createTempFile(".flixxo", ".tmp")) else {

                    val torrentFile = FileHelper.getTorrentDirectory(this.applicationContext, nextUuid ?: "")
                    val torrentSize = viewModel.content.value?.torrentFile?.totalLength?.toLong() ?: 0

                    // File exists
                    if (torrentFile.exists()) {
                        FileHelper.getMedia(this.applicationContext)
                        if (torrentFile.listFiles().filter { it.toString() != ".nomedia" }.count() > 0) {
                            val torrentMp4 = torrentFile.listFiles().filter { it.toString() != ".nomedia" }.first()
                            // File is complete
                            if (torrentMp4.length() == torrentSize) {
                                startVideoPlayer(torrentMp4)
                            } else {
                                torrentFile.deleteRecursively()
                                startTorrentStreaming(torrentSize)

                            }
                        } else {
                            startTorrentStreaming(torrentSize)
                        }
                    } else {
                        startTorrentStreaming(torrentSize)
                    }
                }
            }


            DetailViewModel.UserContentStatus.CanPurchase -> {
                detail_episode.visibility = View.GONE
                pill_next_ep_gone.setBackgroundResource(R.drawable.button_pay_to_watch)
                val detailPrice = nextPrice.formatValue()
                pay_to_watch_next_ep.text = String.format(getString(R.string.detailPrice), detailPrice)
                next_ep_container.visibility = View.VISIBLE
                pay_to_watch_next_ep.visibility = View.VISIBLE

                pill_next_ep_gone.postDelayed({
                    if (playBool) {
                        return@postDelayed
                    }
                    detail_episode.visibility = View.VISIBLE
                    backHome = true
                    pill_next_ep_gone.setBackgroundResource(R.drawable.pill_flixxo_coin)
                    next_ep_container.visibility = View.VISIBLE
                    pay_to_watch_next_ep.visibility = View.GONE

                }, 4000)
            }

            DetailViewModel.UserContentStatus.DontEnoughMoney -> {
                val intent = Intent(this, NoFlixxAdActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun startVideoPlayer(mp4File: File) {

        val intent = Intent(this, MediaPlayerActivity::class.java)
        val subs = if(isNextEpisode) createSubsList(nextSubtitles) else createSubsList(subtitles)
        val uri = if(isNextEpisode) getUriSubtitle(nextSubtitles, nextAudioLang) else getUriSubtitle(subtitles, audioLang)
        //Save last episode in preferences
        intent.putExtra(MediaPlayerActivity.VideoId, "${BuildConfig.INTERACTIVE_SERIE_EPISODE_UUID}-$userResume")
        intent.putExtra(MediaPlayerActivity.NoneUri, Uri.fromFile(FileHelper.getNone(this)))
        intent.putExtra(MediaPlayerActivity.MediaUri, Uri.fromFile(mp4File))
        intent.putExtra(MediaPlayerActivity.SubtitleUri, uri)
        intent.putExtra(MediaPlayerActivity.OpenSubtitlesUserAgent, "TemporaryUserAgent")
        intent.putExtra(MediaPlayerActivity.SubtitleLanguageCode, "en")
        intent.putExtra(MediaPlayerActivity.SubtitlesList, subs)
        if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) intent.putExtra(MediaPlayerActivity.EpisodeList,  FileHelper.getSeriesNavigation(assets)) else intent.putExtra(MediaPlayerActivity.EpisodeList, arrayListOf<SerieInteractivePlayer>())
        startActivityForResult(intent,1)
    }

    private fun startTorrentStreaming(torrentSize: Long) {

        if (FileHelper.isEnoughSpace(this, torrentSize)) {

            val intent = Intent(this, TorrentStreamingActivity::class.java)
            val uuid = if (isNextEpisode) nextUuid else uuid
            val subs = if (isNextEpisode) createSubsList(nextSubtitles) else createSubsList(subtitles)
            val uri = if (isNextEpisode) getUriSubtitle(nextSubtitles, nextAudioLang) else getUriSubtitle(
                subtitles,
                audioLang
            )
            FileHelper.getTorrentDirectory(this.applicationContext, uuid)
            intent.putExtra(
                TorrentStreamingActivity.Companion.NONE_URI,
                Uri.fromFile(FileHelper.getNone(this.applicationContext))
            )
            intent.putExtra(
                TorrentStreamingActivity.Companion.TORRENT_DATA,
                Base64.decode(newTorrentFile.torrentData, Base64.NO_WRAP)
            )
            intent.putExtra(TorrentStreamingActivity.Companion.TORRENT_UUID, uuid)
            intent.putExtra(TorrentStreamingActivity.Companion.TORRENT_IMAGE, media)
            intent.putExtra(TorrentStreamingActivity.Companion.CLIENT_SEED, viewModel.getSeed())
            intent.putExtra(TorrentStreamingActivity.Companion.SUBS, subs)
            intent.putExtra(TorrentStreamingActivity.Companion.SUBTITLE_URI, uri)
            startActivityForResult(intent,1)

        } else {
            showAlertSpace()
        }
    }


    private fun createList() {

        var episodeCount = 0
        var seasonPos = -1
        var epPos = -1
        var expand: Boolean

        episodesList.clear()

        for (i in 0 until seasons.sortedWith(compareBy { it.number }).count()) {

            mutableList.clear()

            var episodePosition = 0
            for (j in 0 until seasons[i].content.count()) {
                val episode = seasons[i].content[j]

                episodeCount++
                episodePosition++
                val wasPurchased = contentPurchased.any { it.uuid.equals(episode.uuid) }
                Timber.i("contentPurchased: ${contentPurchased.size}")
                mutableList.add(EpisodesItem(episode, this, episodePosition, wasPurchased))

                if (wasPurchased) {
                    seasonPos = i
                    epPos = j
                }
            }

            val seasonExpand = preferencesManager.getString("Season_expand")?.toInt()

            expand = if(seasons.size == 1) {
                true
            } else {
                seasonExpand == i + 1
            }


            ExpandableGroup(ExpandableHeaderItem(season = seasons[i], context = this), expand).apply {
                val section = Section()
                section.addAll(mutableList)
                add(section)
                episodesList.add(this)

            }
        }

        customPagerAdapter.refreshEpisodes(episodesList)

        setNextEpisode(seasonPos, epPos)
    }


    private fun setNextEpisode(seasonPos: Int, episodePos: Int) {
        //first season, first episode
        if (seasonPos == -1 && episodePos == -1) {
            showNextEpisode(serie.season.first().number ?: 1, 1, serie.season[0].content[0])
            return
        }

        val season = serie.season[seasonPos]
        val episode = season.content[episodePos]

        //last season, last episode
        if (season == serie.season.last() && episode == serie.season.last().content.last()) {
            showNextEpisode(1, 1, serie.season[0].content[0])
            return
        }

        //last episode, next season
        if (episode == season.content.last()) {
            val nextSeason = serie.season[seasonPos + 1]
            showNextEpisode(nextSeason.number!!, 1, nextSeason.content[0])
            return
        }

        showNextEpisode(season.number!!, episodePos + 2, season.content[episodePos + 1])
    }

    private fun showNextEpisode(season: Int, episode: Int, content: Content) {

        val s = if (season > 9) {
            "$season"
        } else {
            "0$season"
        }

        val e = if (episode > 9) {
            "$episode"
        } else {
            "0$episode"
        }

        detail_episode.text = String.format(getString(R.string.watchNext), s, e)
        pill_next_ep_gone.visibility = View.VISIBLE
        backHome = true
        nextUuid = content.uuid.toString()
        nextPrice = content.price!!
        nextSubtitles = content.subtitle!!
        nextAudioLang = content.audioLang!!
        viewModel.canUserPlay(nextUuid, nextPrice)
        viewModel.getTorrentFile(nextUuid)
        preferencesManager.putString("Season_expand", s)
    }

    private fun loadWatchButoon(purchased: Boolean) {
        progress_watch.visibility = View.VISIBLE

        if (purchased) {
            progress_watch.visibility = View.INVISIBLE
            pill_container.visibility = View.VISIBLE
            flixxo_image_price.visibility = View.GONE
            detail_price.visibility = View.GONE
            separate.visibility = View.GONE
        } else {
            progress_watch.visibility = View.INVISIBLE
            pill_container.visibility = View.VISIBLE
        }

        //hide price in interactive serie
        if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) {
            detail_price.visibility = View.GONE
            separate.visibility = View.GONE
        }
        //show "start again" in interactive serie
        if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID && purchased) {
            start_again.visibility = View.VISIBLE
            watch_now.text = getString(R.string.continue_onBoard)
        }
    }

    override fun onResume() {
        super.onResume()
        hideControlBar(true)
        viewModel.getFollowings()
        pill_next_ep_gone.setBackgroundResource(R.drawable.pill_flixxo_coin)
        pill_gone.setBackgroundResource(R.drawable.pill_flixxo_coin)
        pill_container.setBackgroundResource(R.drawable.pill_flixxo_coin)
        separate.setBackgroundResource(android.R.color.white)
        viewModel.loadBalance()
        val uuid = if(isNextEpisode) nextUuid else uuid
        val price = if(isNextEpisode) nextPrice else price
        if (uuid == BuildConfig.INTERACTIVE_SERIE_UUID) viewModel.canUserPlay(BuildConfig.INTERACTIVE_SERIE_EPISODE_UUID, priceAltEscEpisode) else viewModel.canUserPlay(uuid, price)
    }

    override fun onMediaSelected(uuid: String, contentType: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("UUID_INTENT", uuid)
        intent.putExtra("CONTENT_TYPE_INTENT", 1)
        intent.putExtra("UUID_SERIE", uuidSerie)
        intent.putExtra(SELECTED_CONTENT, selectedContentHome)
        intent.putExtra("SCREEN", "SelectedContent")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        firebaseAnalytics.logEvent("open_content_detail", bundleOf("UUID" to uuid))
        finish()
    }

    override fun search(text: String) {
        bundle = Bundle()
        bundle.putString("SEARCH_TEXT", text)
        val searchFragment = SearchFragment()
        searchFragment.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.detail_view, searchFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun createSubsList(subtitle: ArrayList<Subtitle>?): ArrayList<Subtitle>? {
        subtitle?.forEach {
            it.langName = viewModel.getCompleteLanguage(assets, it.lang!!)
        }
        return subtitle
    }

    private fun getUriSubtitle(subtitle: ArrayList<Subtitle>?, audioLang: String): Uri {
        val userLang = preferencesManager.getString("USER_LANG")
        var uri: String? = ""
        if (audioLang != userLang) {
            subtitle?.firstOrNull { it.lang == userLang }?.let {
                uri = it.url
            }
        }
        return Uri.parse(uri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (selectedContentHome == SERIES_CONTENT_HOME) {
            contentType = CONTENT_TYPE_SERIES
        } else {
            contentType = CONTENT_TYPE_EPISODES
            uuidSerie = uuid
        }

        val intent = Intent(this, DetailActivity::class.java)
        if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) {
            intent.putExtra("UUID_INTENT", BuildConfig.INTERACTIVE_SERIE_UUID)
            intent.putExtra("CONTENT_TYPE_INTENT", 2)
        } else {
            intent.putExtra("UUID_INTENT", uuidSerie)
            intent.putExtra("UUID_SERIE", uuidSerie)
            intent.putExtra(SELECTED_CONTENT, selectedContentHome)
            intent.putExtra("SCREEN", "Detail")
            if (resultCode == 0 && contentType == CONTENT_TYPE_SERIES) {
                intent.putExtra("TAB", TAB_EPISODES)
            } else {
                intent.putExtra("TAB", TAB_INFORMATION)
            }
            intent.putExtra("CONTENT_TYPE_INTENT", contentType)
        }
        startActivity(intent)
        finish()

    }

    private fun showAlertSpace() {
        val alert = AlertDialog.Builder(this, R.style.CustomDialog)
        alert.setTitle(getString(R.string.notEnoughSpace))
        alert.setMessage(getString(R.string.goConfigurationMessage))
        alert.setPositiveButton(getString(R.string.goConfiguration)) { _, _ ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ITEMID", R.id.settings)
            intent.putExtra("UUID_INTENT", uuid)
            intent.putExtra("CONTENT_TYPE_INTENT", contentType)
            startActivity(intent)
            true
            pill_next_ep_gone.setBackgroundResource(R.drawable.pill_flixxo_coin)
            pill_gone.setBackgroundResource(R.drawable.pill_flixxo_coin)
            pill_container.setBackgroundResource(R.drawable.pill_flixxo_coin)
            separate.setBackgroundResource(android.R.color.white)
        }
        alert.setNegativeButton(getString(R.string.cancelLower)) { _, _ ->
            pill_next_ep_gone.setBackgroundResource(R.drawable.pill_flixxo_coin)
            pill_gone.setBackgroundResource(R.drawable.pill_flixxo_coin)
            pill_container.setBackgroundResource(R.drawable.pill_flixxo_coin)
            separate.setBackgroundResource(android.R.color.white)
        }
        alert.setCancelable(false)
        alert.show()
    }
}