package com.flixxo.apps.flixxoapp.view

import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.model.Season

enum class SerieDetailRow {
    ROW,
    HEADER
}

data class SerieDetailWrapper(
    val type: SerieDetailRow,
    val content: Content? = null,
    val season: Season? = null
)
