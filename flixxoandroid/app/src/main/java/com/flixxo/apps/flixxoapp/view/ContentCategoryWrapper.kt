package com.flixxo.apps.flixxoapp.view

import com.flixxo.apps.flixxoapp.model.ContentCategory

enum class HomeMediaType {
    AD,
    MEDIA
}

data class ContentCategoryWrapper(
    val contentCategory: ContentCategory?,
    val type: HomeMediaType
)