package com.flixxo.apps.flixxoapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import com.flixxo.apps.flixxoapp.utils.FileHelper
import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentandroid.TorrentSession
import com.masterwok.simpletorrentandroid.TorrentSessionOptions
import com.masterwok.simpletorrentandroid.contracts.TorrentSessionListener
import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus
import timber.log.Timber


class TorrentService : Service(), TorrentSessionListener {

    private var listener: TorrentSessionListener? = null
    lateinit var torrentSession: TorrentSession
    private val mBinder = ServiceBinder()
    private var mWakeLock: PowerManager.WakeLock? = null
    private lateinit var libTorrentThread: HandlerThread
    private lateinit var torrentUuid: String

    companion object {
        fun bindHere(context: Context, serviceConnection: ServiceConnection) {
            val torrentServiceIntent = Intent(context, TorrentService::class.java)
            context.bindService(torrentServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        fun start(context: Context) {
            val torrentServiceIntent = Intent(context, TorrentService::class.java)
            context.startService(torrentServiceIntent)
        }
    }


    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    private fun getTorrentOptions(): TorrentSessionOptions {
        return TorrentSessionOptions(
            downloadLocation = FileHelper.getTorrentDirectory(this.applicationContext, torrentUuid)
            , onlyDownloadLargestFile = false
            , enableLogging = false
            , shouldStream = false
        )
    }

    fun startStreaming(torrentUrl: ByteArray, seed: String, uuid: String) {
        torrentUuid = uuid
        torrentSession = TorrentSession(getTorrentOptions())

        Timber.i("Starting torrentSession streaming")
        Timber.i(torrentUrl.toString())
        if (torrentSession.isRunning) {
            Timber.i("Torrent is already running")
            return
        }

        torrentSession.listener = this

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TorrentService:WakeLock")
        mWakeLock?.acquire(1000)

        Timber.i("Torrent session start")
        libTorrentThread = HandlerThread("Somethread")
        libTorrentThread.start()
        val libTorrentHandler = Handler(libTorrentThread.looper)
        libTorrentHandler.post {
            torrentSession.start(torrentUrl, seed)
        }
    }

    fun stopStreaming() {
        Timber.i("Stopping torrentSession streaming")
        torrentSession.listener = null

        if (!torrentSession.isRunning) {
            return
        }

        torrentSession.stop()
    }

    fun setListener(listener: TorrentSessionListener) {
        this.listener = listener
    }

    fun removeListener() {
        this.listener = null
    }

    inner class ServiceBinder : Binder() {
        val service: TorrentService
            get() = this@TorrentService
    }

    //Torrent events
    override fun onAddTorrent(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onAddTorrent")
        this.listener?.onAddTorrent(torrentHandle, torrentSessionStatus)
    }

    override fun onBlockUploaded(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onBlockUploaded")
        this.listener?.onBlockUploaded(torrentHandle, torrentSessionStatus)
    }


    override fun onMetadataFailed(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onMetadataFailed")
        this.listener?.onMetadataFailed(torrentHandle, torrentSessionStatus)
    }


    override fun onMetadataReceived(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onMetadataReceived")
        this.listener?.onMetadataReceived(torrentHandle, torrentSessionStatus)

        Timber.i("progress: $torrentSessionStatus.progress")
    }

    override fun onPieceFinished(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onPieceFinished")
        this.listener?.onPieceFinished(torrentHandle, torrentSessionStatus)
    }

    override fun onTorrentDeleteFailed(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentDeleteFailed")
        this.listener?.onTorrentDeleteFailed(torrentHandle, torrentSessionStatus)
    }

    override fun onTorrentDeleted(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentDeleted")
        this.listener?.onTorrentDeleted(torrentHandle, torrentSessionStatus)
    }

    override fun onTorrentError(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentError")
        this.listener?.onTorrentError(torrentHandle, torrentSessionStatus)
    }

    override fun onTorrentFinished(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentFinished")
        this.listener?.onTorrentFinished(torrentHandle, torrentSessionStatus)
    }

    override fun onTorrentPaused(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentPaused")
        this.listener?.onTorrentPaused(torrentHandle, torrentSessionStatus)
    }

    override fun onTorrentRemoved(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentRemoved")
        this.listener?.onTorrentRemoved(torrentHandle, torrentSessionStatus)
    }

    override fun onTorrentResumed(torrentHandle: TorrentHandle, torrentSessionStatus: TorrentSessionStatus) {
        Timber.i("onTorrentResumed")
        this.listener?.onTorrentResumed(torrentHandle, torrentSessionStatus)
    }


}
