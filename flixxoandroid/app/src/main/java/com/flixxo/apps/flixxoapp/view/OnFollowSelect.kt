package com.flixxo.apps.flixxoapp.view

import com.flixxo.apps.flixxoapp.model.Author
import com.flixxo.apps.flixxoapp.model.FollowState

interface OnFollowSelect {
    fun userSelected(user: FollowState, pos: Int)
    fun isFollowing(user: Author): Boolean
    fun followUser(id: String, pos: Int)
    fun unfollowUser(id: String, pos: Int)
}