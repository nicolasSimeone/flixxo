package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.*
import com.flixxo.apps.flixxoapp.viewModel.MainViewModel
import com.frostwire.jlibtorrent.swig.libtorrent_jni
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_header.*
import kotlinx.android.synthetic.main.my_custom_view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    private var uuidDetail: String = ""
    private var contentTypeDetail: Int = 0
    var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        LocaleHelper.onAttach(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setViewModelObservers()
        setClickListeners()

        back_button_header.setOnClickListener {
            onBackPressed()
        }

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
            val itemId = intent.extras?.getInt("ITEMID") ?: 0
            if (itemId > 0)
                setMenuFromDetail(itemId)
        } // Else, need to wait for onRestoreInstanceState

        createSeed()
        deleteCacheByTime()

        var isNotification = false
        val uuid = intent.extras?.get("UUID") ?: ""
        val contentType = intent.extras?.get("CONTENT_TYPE") ?: ""

        uuidDetail = intent.extras?.getString("UUID_INTENT") ?: ""
        contentTypeDetail = intent.extras?.getInt("CONTENT_TYPE_INTENT") ?: 0


        if (uuid == "") {
            return
        } else {
            val intent = Intent(this, DetailActivity::class.java)
            isNotification = true
            intent.putExtra("UUID_NOTIFICATION", uuid as String)
            intent.putExtra("IS_NOTIFICATION", isNotification)
            val contentTypeInt = Integer.parseInt(contentType as String)
            intent.putExtra("CONTENT_TYPE_NOTIFICATION", contentTypeInt)
            startActivity(intent)
        }


        no_internet_connection.tryAgainAction = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val connectivity = ConnectivityHelper.getConnectionType(this)
                if (connectivity == NetworkCapabilities.TRANSPORT_CELLULAR or NetworkCapabilities.TRANSPORT_WIFI) {
                    no_internet_connection.visibility = View.GONE
                    nav_host_container.visibility = View.VISIBLE
                    home_header.visibility = View.VISIBLE
                    recreate()
                }
            } else {
                val connectivity = ConnectivityHelper.getConnectionTypeSDK21(this)
                if (connectivity == 0 or ConnectivityManager.TYPE_WIFI) {
                    no_internet_connection.visibility = View.GONE
                    nav_host_container.visibility = View.VISIBLE
                    home_header.visibility = View.VISIBLE
                    recreate()
                }
            }
        }


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
        val itemId = intent.extras?.getInt("ITEMID") ?: 0
        if (itemId > 0)
            setMenuFromDetail(itemId)


    }

    fun setMenuFromDetail(itemId: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        var item = 0
        when (itemId) {
            R.id.home -> {
                bottomNavigationView.selectedItemId = R.id.home
                item = 0
                true
            }
            R.id.gamification -> {
                bottomNavigationView.selectedItemId = R.id.gamification
                item = 1
                true
            }
            R.id.search -> {
                bottomNavigationView.selectedItemId = R.id.search
                item = 2
                true
            }

            R.id.settings -> {
                bottomNavigationView.selectedItemId = R.id.settings
                item = 3
                true
            }
        }

        bottomNavigationView.menu.getItem(item).isChecked = true
        bottomNavigationView.performClick()
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadBalance()
        viewModel.loadAdReward()
        hideControlBar(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        hideControlBar(hasFocus)
    }

    /**
     * Overriding popBackStack is necessary in this case if the app is started from the deep link.
     */
    override fun onBackPressed() {
        when {
            currentNavController?.value?.currentDestination?.label == "Home" -> AlertDialog.Builder(
                this,
                R.style.CustomDialog
            )
                .setTitle(getString(R.string.are_you_sure))
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, id -> this.finishAffinity();System.exit(0) }
                .setNegativeButton(getString(R.string.no), null)
                .show()

            uuidDetail != "" -> {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("UUID_INTENT", uuidDetail)
                intent.putExtra("CONTENT_TYPE_INTENT", contentTypeDetail)
                startActivity(intent)
                uuidDetail = ""
                contentTypeDetail = 0

            }
            else -> super.onBackPressed()
        }


    }

    private fun setViewModelObservers() {
        viewModel.balance.observe(this, Observer { value ->
            value?.let {
                home_header.balance.text = value.amount.formatValue()
            }
        })
    }

    private fun setClickListeners() {
        earnButton.setOnClickListener {
            startActivity(Intent(this, AdPlayerActivity::class.java))
        }
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // Setup the bottom home view with a list of home graphs
        val navGraphIds =
            listOf(R.navigation.home, R.navigation.gamification, R.navigation.search, R.navigation.settings)
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        controller.observe(this, Observer { navController ->
            setupToolbarFromNavController(navController)
        })
        currentNavController = controller


    }

    private fun createSeed() {
        val seed = libtorrent_jni.create_seed()
        viewModel.registerClientSeed(seed)
        val publicKey = libtorrent_jni.create_public_key(seed)
        viewModel.updateClientKey(publicKey)
        Timber.i("seed: $seed - publicKey: $publicKey")
    }

    private fun deleteCacheByTime() {
        FileHelper.deleteCacheByTime(this)
    }

}

fun MainActivity.setupToolbarFromNavController(navController: NavController) {
    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        when (destination.id) {
            R.id.accountFragment, R.id.editProfileFragment, R.id.depositFragment, R.id.userProfileFragment, R.id.followFragment -> {
                home_header.visibility = View.GONE
                custom_header.visibility = View.VISIBLE
                title_header.text = destination.label
            }
            R.id.searchFragment -> {
                home_header.visibility = View.GONE
                custom_header.visibility = View.GONE
            }
            else -> {
                home_header.visibility = View.VISIBLE
                custom_header.visibility = View.GONE
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        when (ConnectivityHelper.getConnectionType(this)) {
            NetworkCapabilities.TRANSPORT_WIFI -> {
                ConnectivityHelper.logEvent(NetworkCapabilities.TRANSPORT_WIFI, this)
                no_internet_connection.visibility = View.GONE
                nav_host_container.visibility = View.VISIBLE
            }
            NetworkCapabilities.TRANSPORT_CELLULAR -> {
                ConnectivityHelper.logEvent(NetworkCapabilities.TRANSPORT_CELLULAR, this)
                no_internet_connection.visibility = View.GONE
                nav_host_container.visibility = View.VISIBLE
            }

            else -> {
                ConnectivityHelper.logEvent(-1, this)
                no_internet_connection.visibility = View.VISIBLE
                nav_host_container.visibility = View.GONE
                home_header.visibility = View.GONE
                hideControlBar(true)
            }
        }
    } else {
        when (ConnectivityHelper.getConnectionTypeSDK21(this)) {
            ConnectivityManager.TYPE_WIFI -> {
                ConnectivityHelper.logEvent(ConnectivityManager.TYPE_WIFI, this)
                no_internet_connection.visibility = View.GONE
                nav_host_container.visibility = View.VISIBLE
            }
            0 -> {
                ConnectivityHelper.logEvent(0, this)
                no_internet_connection.visibility = View.GONE
                nav_host_container.visibility = View.VISIBLE
            }

            else -> {
                ConnectivityHelper.logEvent(-1, this)
                no_internet_connection.visibility = View.VISIBLE
                nav_host_container.visibility = View.GONE
                home_header.visibility = View.GONE
                hideControlBar(true)
            }
        }
    }
}