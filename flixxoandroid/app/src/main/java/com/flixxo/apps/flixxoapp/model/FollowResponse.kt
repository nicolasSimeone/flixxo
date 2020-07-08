package com.flixxo.apps.flixxoapp.model

data class FollowResponse(
    val id: Int?,
    val uuid: String? = "",
    val nickname: String? = "",
    val profile: Followers
)