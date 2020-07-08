package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewTorrentFile(
    var infoHash: String = "",
    var totalLength: String = "",
    var torrentData: String = "",
    var pieceLength: String = "",
    var lastPieceLength: String = "",
    var torrentFile: TorrentFile
) :
    Parcelable