package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TorrentFile(
    var infoHash: String = "",
    var totalLength: String = "",
    var type: String = "",
    var data: ByteArray = ByteArray(0)
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TorrentFile

        if (infoHash != other.infoHash) return false
        if (totalLength != other.totalLength) return false
        if (type != other.type) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = infoHash.hashCode()
        result = 31 * result + totalLength.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}