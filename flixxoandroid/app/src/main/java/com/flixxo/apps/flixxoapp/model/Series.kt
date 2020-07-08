package com.flixxo.apps.flixxoapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Series(
    val cancelComment: String?,
    val isIndexable: Boolean?,
    val body: String? = "",
    val uuid: String? = "",
    val bodyFormat: Int?,
    val title: String? = "",
    val status: Int?,
    val moderationStatus: Int?,
    val audioLang: String? = "",
    val credits: String?,
    val rating: Int?,
    val tags: List<Tag>,
    val author: Author,
    val category: Category?,
    val media: List<Media>?,
    var season: List<Season>

) : Parcelable {
    fun getLang(): String? {
        return season.first().content.first().audioLang
    }

    fun convertToContent(): Content {
        return Content(
            cancelComment = cancelComment,
            isIndexable = isIndexable,
            body = body,
            uuid = uuid,
            title = title,
            audioLang = getLang(),
            author = author,
            category = category,
            media = media,
            contentType = 2
        )
    }

    fun getMainImage(): String? {
        return sortedMedia()[0].sizes.cover
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

