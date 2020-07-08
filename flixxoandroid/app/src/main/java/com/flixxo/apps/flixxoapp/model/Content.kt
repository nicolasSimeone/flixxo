package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import com.masterwok.simplevlcplayer.fragments.Subtitle
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Content(
    val price: Double? = 0.0,
    val cancelComment: String? = "",
    val isIndexable: Boolean? = false,
    val body: String? = "",
    val uuid: String? = "",
    val contentType: Int? = 0,
    val title: String? = "",
    val audioLang: String? = "",
    val categoryID: Long? = 0,
    val category: Category? = Category(),
    val releaseDate: String? = "",
    val updatedAt: String? = "",
    val createdAt: String? = "",
    val duration: Long? = 0,
    val downloadStatus: Long? = 0,
    val authorFee: String? = "",
    val seederFee: String? = "",
    val author: Author? = Author(),
    val media: List<Media>? = listOf(),
    val contentMedia: List<Media>? = listOf(),
    val torrentFile: TorrentFile = TorrentFile(),
    val subtitle: ArrayList<Subtitle>? = arrayListOf(),
    val cover: Cover = Cover(Sizes())

) : Parcelable {
    fun getMainImage(): String? {
        if (sortedMedia().count() == 0) return "" else {
            return sortedMedia()[0].sizes.cover
        }
    }

    fun getThumbImage(): String? {
        val media = sortedMedia()

        if (media.isEmpty()) {
            return ""
        }

        val position = if (media.count() == 1) 0 else 1
        return media[position].sizes.xcover
    }

    fun getThumbImageSearch(): String {
        return cover.sizes.xcover ?: ""
    }

    fun getThumbImageContent(): String {
        val media = sortedMedia()
        if (media.count() == 0) {
            return ""
        } else {
            val item = media.firstOrNull { it.type?.toInt() == 2 }
            return item?.sizes?.xcover ?: ""
        }
    }

    private fun sortedMedia(): List<Media> {
        return media!!.sortedWith(Comparator { a, b ->
            val firstItemOrder = a.order ?: 99
            val secondItemOrder = b.order ?: 99
            when {
                firstItemOrder > secondItemOrder -> 1
                firstItemOrder < secondItemOrder -> -1
                else -> 0
            }
        })
    }
}