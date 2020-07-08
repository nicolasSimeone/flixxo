package com.flixxo.apps.flixxoapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class NetworkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.net.conn.CONNECTIVITY_CHANGE") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ConnectivityHelper.getConnectionType(context!!)
            } else {
                ConnectivityHelper.getConnectionTypeSDK21(context!!)
            }
        }
    }
}