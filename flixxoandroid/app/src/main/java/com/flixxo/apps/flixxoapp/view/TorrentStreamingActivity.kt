package com.flixxo.apps.flixxoapp.view

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.service.TorrentService
import com.flixxo.apps.flixxoapp.utils.FileHelper
import com.flixxo.apps.flixxoapp.utils.LocaleHelper
import com.flixxo.apps.flixxoapp.utils.hideControlBar
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.CLIENT_SEED
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.DURATION_ANIMATION
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.INTERVAL
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.MINOR_PROGRESS
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.NONE_URI
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.SUBS
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.SUBTITLE_URI
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.TORRENT_DATA
import com.flixxo.apps.flixxoapp.view.TorrentStreamingActivity.Companion.TORRENT_UUID
import com.flixxo.apps.flixxoapp.viewModel.TorrentStreamingViewModel
import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentandroid.contracts.TorrentSessionListener
import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus
import com.masterwok.simplevlcplayer.VlcOptionsProvider
import com.masterwok.simplevlcplayer.activities.MediaPlayerActivity
import com.masterwok.simplevlcplayer.fragments.Subtitle
import kotlinx.android.synthetic.main.activity_torrent_streaming.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask


class TorrentStreamingActivity : AppCompatActivity(), TorrentSessionListener {

    enum class States { WAITING, DOWNLOADING, COMPLETED, ERROR, PRELOADING }

    object Companion {
        const val TORRENT_DATA: String = "TORRENT_DATA"
        const val TORRENT_UUID: String = "TORRENT_UUID"
        const val TORRENT_IMAGE: String = "TORRENT_IMAGE"
        const val CLIENT_SEED: String = "CLIENT_SEED"
        const val MINOR_PROGRESS: Long = 0
        const val DURATION_ANIMATION: Long = 1000
        const val SUBS: String = "SUBTITLES_LIST"
        const val INTERVAL: Long = 10000
        const val SUBTITLE_URI: String = "SUBTITLE_URI"
        const val NONE_URI: String = "NONE_URI"
    }

    private val viewModel: TorrentStreamingViewModel by viewModel()
    var torrentService: TorrentService? = null
    private var torrentUri: ByteArray? = null
    private var state: States? = null
    private var seed: String? = null
    private var isPlaying = false

    private lateinit var uuid: String
    private lateinit var subs: ArrayList<Subtitle>
    private lateinit var subtitleUri: Uri
    private lateinit var noneUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocaleHelper.onAttach(this)
        setContentView(R.layout.activity_torrent_streaming)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        viewModel.messages.observe(this, androidx.lifecycle.Observer {
            val messages = it
            var index = 0
            val quotesList = messages.load_messages
            val timer = Timer()
            timer.schedule(timerTask {
                runOnUiThread {
                    if (index >= quotesList.size) {
                        index = 0
                    }
                    random_quote.startAnimation(setCrossfadeAnimation(1.0f, 0.2f))
                    random_quote.text = quotesList[index]
                    random_quote.startAnimation(setCrossfadeAnimation(0.2f, 1.0f))
                    index++
                }
            }, MINOR_PROGRESS, INTERVAL)
        })

        torrentUri = intent?.extras?.get(TORRENT_DATA) as ByteArray

        if (torrentUri == null) {
            Timber.e("Not torrentUri found!")
            return
        }

        seed = intent?.extras?.get(CLIENT_SEED) as String?

        intent?.extras?.get(TORRENT_UUID)?.let {
            uuid = it as String
        }

        intent?.extras?.get(SUBS)?.let {
            subs = it as ArrayList<Subtitle>
        }

        intent?.extras?.get(SUBTITLE_URI)?.let {
            subtitleUri = it as Uri
        }

        intent?.extras?.get(NONE_URI)?.let {
            noneUri = it as Uri
        }

        // VlcOptionsProvider can be used to provide LibVlc initialization options.
        VlcOptionsProvider.getInstance().options = VlcOptionsProvider
            .Builder(this)
            .setVerbose(true)
            // See R.array.subtitles_encoding_values
            .withSubtitleEncoding("KOI8-R")
            .build()



        viewModel.getLoadingMessages()

        cancel_loader.setOnClickListener {
            finish()
            torrentService?.stopStreaming()
        }
        hideControlBar(true)
    }

    override fun onStart() {
        super.onStart()
        TorrentService.bindHere(this, mServiceConnection)
    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            torrentService = (service as TorrentService.ServiceBinder).service
            torrentService?.setListener(this@TorrentStreamingActivity)

            try {
                torrentService?.startStreaming(torrentUrl = torrentUri!!, seed = seed!!, uuid = uuid)
            } catch (e: Exception) {
                this@TorrentStreamingActivity.finish()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            torrentService?.removeListener()
            torrentService = null
        }
    }

    override fun onResume() {
        super.onResume()

        state?.let {
            setState(state!!)
        } ?: run {
            setState(States.WAITING)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        torrentService?.stopStreaming()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
        torrentService?.let {
            unbindService(mServiceConnection)
            torrentService = null
        }
    }


    private fun setState(state: States) {
        this.state = state
        updateView(state, null)
    }

    private fun updateView(state: States, torrentSessionStatus: TorrentSessionStatus?) {

        runOnUiThread {
            if (state == States.DOWNLOADING) {
                loading_text.text = getString(R.string.loading_torrent)
            } else {
                loading_text.text = getString(R.string.connecting_torrent)
            }

//            torrentSessionStatus?.let {
//                progress_state.text = (torrentSessionStatus.progress * 100).toInt().toString() + " %"
//                state_text.text = state.name
//            }
        }
    }

    override fun onAddTorrent(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        updateView(States.DOWNLOADING, torrentSessionStatus)
    }

    override fun onBlockUploaded(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
    }

    override fun onMetadataFailed(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
    }

    override fun onMetadataReceived(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {

        updateView(States.DOWNLOADING, torrentSessionStatus)

        Timber.i(torrentSessionStatus.saveLocationUri.toString())
        Timber.i(torrentSessionStatus.videoFileUri.toString())
    }

    private var torrentProgress = 0F

    override fun onPieceFinished(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {


        torrentProgress = torrentSessionStatus.progress * 100
        Timber.i("torrentProgress: $torrentProgress")
        updateView(States.DOWNLOADING, torrentSessionStatus)

        try {
            if (!isPlaying) {

                val pathTo = torrentSessionStatus.videoFileUri.path
                val localFile = File(pathTo)
                val retriever = MediaMetadataRetriever()

                localFile.setReadable(true)
                Timber.i("torrentVideo Checking file: $pathTo")
                retriever.setDataSource(pathTo)


                val hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)
                val isVideo = "yes" == hasVideo
                Timber.i("torrentVideo State: $isVideo")
                if (isVideo) {
                    FileHelper.getMedia(this)
                    isPlaying = true
                    Timber.i("torrentVideo Playing video at  $torrentProgress")
                    startMediaPlayerActivity(torrentSessionStatus.videoFileUri, subtitleUri, subs)
                }

            }
        } catch (e: Exception) {
            isPlaying = false
            Timber.i("torrentVideo is NOT valid video")
        }
    }


    override fun onTorrentDeleteFailed(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
    }

    override fun onTorrentDeleted(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
    }

    override fun onTorrentError(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        updateView(States.ERROR, torrentSessionStatus)

        Timber.i("TORRRENT_ERROR")
        Timber.i(torrentHandle.status().errorCode().message())
        torrentService?.stopStreaming()
        finish()
    }


    override fun onTorrentFinished(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {

        if (torrentSessionStatus.torrentSessionBuffer.allPiecesAreDownloaded() && !isPlaying) {

            updateView(States.COMPLETED, torrentSessionStatus)
            Timber.i("Playing video")
            startMediaPlayerActivity(torrentSessionStatus.videoFileUri, subtitleUri, subs)
        }
    }

    override fun onTorrentPaused(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentPaused")
    }

    override fun onTorrentRemoved(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentRemoved")
    }

    override fun onTorrentResumed(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentResumed")
    }


    private val videoPlayerRequestCode = 10293

    /**
     * Start the simple-vlc-player media player activity. Subtitle must be local
     * file Uri as it appears libVLC does not support adding subtitle using a
     * FileDescriptor (like Media instance).
     *
     * @param videoUri    The selected video URI.
     * @param subtitleUri The selected subtitle URI (must be local file URI).
     */
    private fun startMediaPlayerActivity(videoUri: Uri?, subtitleUri: Uri?, subs: ArrayList<Subtitle>) =
        startActivityForResult(Intent(this, MediaPlayerActivity::class.java).apply {
            putExtra(MediaPlayerActivity.MediaUri, videoUri)
            putExtra(MediaPlayerActivity.SubtitleUri, subtitleUri)
            putExtra(MediaPlayerActivity.SubtitlesList, subs)
            putExtra(MediaPlayerActivity.NoneUri, noneUri)
            putExtra(MediaPlayerActivity.SubtitleDestinationUri, Uri.fromFile(cacheDir))

            // This should be the User-Agent you registered with opensubtitles.org
            // See: http://trac.opensubtitles.org/projects/opensubtitles/wiki/DevReadFirst
            putExtra(MediaPlayerActivity.OpenSubtitlesUserAgent, "TemporaryUserAgent")

            // See R.array.language_values
            putExtra(MediaPlayerActivity.SubtitleLanguageCode, "rus")
        }, videoPlayerRequestCode)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == videoPlayerRequestCode && resultCode == Activity.RESULT_CANCELED) {
            torrentService?.stopStreaming()
            finish()
        }
    }

    private fun setCrossfadeAnimation(firstAlpha: Float, secondAlpha: Float): AlphaAnimation {
        val animation = AlphaAnimation(firstAlpha, secondAlpha)
        animation.fillAfter = false
        animation.duration = DURATION_ANIMATION
        animation.repeatCount = 0
        animation.repeatMode = Animation.REVERSE
        return animation
    }
}
