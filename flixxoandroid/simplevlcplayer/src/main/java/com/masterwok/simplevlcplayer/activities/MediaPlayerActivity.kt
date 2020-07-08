package com.masterwok.simplevlcplayer.activities

import android.app.Activity
import android.content.*
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.masterwok.simplevlcplayer.R
import com.masterwok.simplevlcplayer.dagger.injectors.InjectableAppCompatActivity
import com.masterwok.simplevlcplayer.fragments.CastPlayerFragment
import com.masterwok.simplevlcplayer.fragments.LocalPlayerFragment
import com.masterwok.simplevlcplayer.fragments.SerieInteractivePlayer
import com.masterwok.simplevlcplayer.fragments.Subtitle
import com.masterwok.simplevlcplayer.services.MediaPlayerService
import com.masterwok.simplevlcplayer.services.binders.MediaPlayerServiceBinder



class MediaPlayerActivity : InjectableAppCompatActivity() {

    companion object {
        @JvmStatic
        val VideoId = "extra.videoid"
        @JvmStatic
        val MediaUri = "extra.mediauri"
        @JvmStatic
        val NoneUri = "extra.nonuri"
        @JvmStatic
        val SubtitleUri = "extra.subtitleuri"
        @JvmStatic
        val SubtitleDestinationUri = "extra.subtitledestinationuri"
        @JvmStatic
        val SubtitleLanguageCode = "extra.subtitlelanguagecode"
        @JvmStatic
        val OpenSubtitlesUserAgent = "extra.useragent"
        @JvmStatic
        val SubtitlesList = "extra.subtitlelist"
        @JvmStatic
        val EpisodeList = "extra.episodelist"
    }

    private var mediaPlayerServiceBinder: MediaPlayerServiceBinder? = null
    private var localPlayerFragment: LocalPlayerFragment? = null
    private var castPlayerFragment: CastPlayerFragment? = null

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return

            when (action) {
                MediaPlayerService.RendererClearedAction -> showLocalPlayerFragment()
                MediaPlayerService.RendererSelectionAction -> showCastPlayerFragment()
            }
        }
    }

    private val mediaPlayerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mediaPlayerServiceBinder = iBinder as MediaPlayerServiceBinder

            registerMediaController(iBinder)

            if (mediaPlayerServiceBinder?.selectedRendererItem == null) {
                showLocalPlayerFragment()
            } else {
                showCastPlayerFragment()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mediaPlayerServiceBinder = null

            mediaController?.unregisterCallback(controllerCallback)
        }
    }

    private fun registerMediaController(serviceBinder: MediaPlayerServiceBinder?) {
        if (serviceBinder == null) {
            return
        }

        mediaController = MediaController(
            this,
            serviceBinder.mediaSession!!.sessionToken
        ).apply {
            registerCallback(controllerCallback)
        }

    }

    private val controllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState) {
            localPlayerFragment?.configure(state)
            castPlayerFragment?.configure(state)
        }
    }

    private fun getLocalPlayerFragment(): LocalPlayerFragment = supportFragmentManager
        .findFragmentByTag(LocalPlayerFragment.Tag) as? LocalPlayerFragment
        ?: LocalPlayerFragment.createInstance(
            videoId = intent.getStringExtra(VideoId) ?: ""
            , mediaUri = intent.getParcelableExtra(MediaUri)
            , noneUri = intent.getParcelableExtra(NoneUri)
            , subtitleUri = intent.getParcelableExtra(SubtitleUri)
            , openSubtitlesUserAgent = intent.getStringExtra(OpenSubtitlesUserAgent)
            , subtitleLanguageCode = intent.getStringExtra(SubtitleLanguageCode)
            , subtitlesList = intent.getSerializableExtra(SubtitlesList) as? ArrayList<Subtitle>
            , episodeList = intent.getSerializableExtra(EpisodeList) as? ArrayList<SerieInteractivePlayer>
        )

    private fun getCastPlayerFragment(): CastPlayerFragment = supportFragmentManager
        .findFragmentByTag(CastPlayerFragment.Tag) as? CastPlayerFragment
        ?: CastPlayerFragment.createInstance(
             mediaUri = intent.getParcelableExtra(MediaUri)
            , subtitleUri = intent.getParcelableExtra(SubtitleUri)
            , subtitleDestinationUri = intent.getParcelableExtra(SubtitleDestinationUri)
            , openSubtitlesUserAgent = intent.getStringExtra(OpenSubtitlesUserAgent)
            , subtitleLanguageCode = intent.getStringExtra(SubtitleLanguageCode)
        )

    private fun showFragment(
        fragment: Fragment
        , tag: String
    ) = supportFragmentManager
        .beginTransaction()
        .replace(R.id.framelayout_fragment_container, fragment, tag)
        .commit()

    private fun showLocalPlayerFragment() {
        castPlayerFragment = null
        localPlayerFragment = getLocalPlayerFragment()

        showFragment(localPlayerFragment!!, LocalPlayerFragment.Tag)
    }

    private fun showCastPlayerFragment() {
        localPlayerFragment = null
        castPlayerFragment = getCastPlayerFragment()

        showFragment(castPlayerFragment!!, CastPlayerFragment.Tag)
    }

    private fun registerRendererBroadcastReceiver() = LocalBroadcastManager
        .getInstance(this)
        .registerReceiver(broadCastReceiver, IntentFilter().apply {
            addAction(MediaPlayerService.RendererClearedAction)
            addAction(MediaPlayerService.RendererSelectionAction)
        })

    private fun bindMediaPlayerService() = bindService(
        Intent(applicationContext, MediaPlayerService::class.java)
        , mediaPlayerServiceConnection
        , Context.BIND_AUTO_CREATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_media_player)
    }

    override fun onStart() {
        super.onStart()

        bindMediaPlayerService()
        registerRendererBroadcastReceiver()

        startService(Intent(applicationContext, MediaPlayerService::class.java))
    }

    override fun onStop() {
        unbindService(mediaPlayerServiceConnection)

        mediaController?.unregisterCallback(controllerCallback)

        LocalBroadcastManager
            .getInstance(this)
            .unregisterReceiver(broadCastReceiver)

        castPlayerFragment = null

        super.onStop()
    }

    override fun onBackPressed() {
        // Always ensure that we stop the media player service when navigating back.
        stopService(Intent(applicationContext, MediaPlayerService::class.java))
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        hideControlBar(true)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
    fun hideControlBar(hasFocus: Boolean) {
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }


}