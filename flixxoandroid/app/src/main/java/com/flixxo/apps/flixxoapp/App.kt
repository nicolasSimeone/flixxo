package com.flixxo.apps.flixxoapp

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.flixxo.apps.flixxoapp.di.*
import com.flixxo.apps.flixxoapp.service.TorrentService
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(
            this,
            listOf(
                apiModule,
                dbModule,
                userModule,
                loginModule,
                contentModule,
                torrentModule,
                mailModule,
                popupModule,
                registerModule,
                codeModule
            )
        )
        AppCenter.start(this@App, "81a9862e-0b93-4fa8-8776-c7993cc9c415", Analytics::class.java, Crashes::class.java)

        Fresco.initialize(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        TorrentService.start(this)
    }

}