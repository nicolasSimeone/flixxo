package com.flixxo.apps.flixxoapp.utils

class TorrentHelper {

    companion object {
        fun getMagnetForTorrent(hash: String, trackerUrl: String): String {
            return "magnet:?xt=urn:btih:$hash&tr=$trackerUrl"
        }
    }
}