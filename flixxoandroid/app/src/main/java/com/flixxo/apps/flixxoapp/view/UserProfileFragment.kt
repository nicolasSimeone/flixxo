package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.loadFromCustom
import com.flixxo.apps.flixxoapp.viewModel.UserProfileViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserProfileFragment : Fragment(), OnItemSelected {

    companion object {
        const val seriesContent = "0"
        const val communityContent = "1"
        const val CONTENT_TYPE_SERIES = 2
    }

    private val viewModel: UserProfileViewModel by viewModel()
    private var authorName: String = ""
    private lateinit var videoAdapter: VideosAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle
    private var type: Int = 0
    private var selectedContent: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        videoAdapter = VideosAdapter(this)

        val authorProfile = arguments?.getString("Author")
        val authorAvatar = arguments?.getString("Avatar")
        var authorFollowing = arguments?.getBoolean("State") as Boolean

        if (authorAvatar == "") avatar_user_profile.setImageResource(R.drawable.ic_profile_user) else avatar_user_profile.loadFromCustom(
            authorAvatar.toString()
        )

        authorProfile?.let {
            user_profile_name.text = it
        } ?: run {
            user_profile_name.text = ""
        }

        if (!authorFollowing) {
            button_following_user.visibility = View.GONE
            button_follow_user.visibility = View.VISIBLE
        } else {
            button_following_user.visibility = View.VISIBLE
            button_follow_user.visibility = View.GONE
        }

        val authorId = arguments?.get("ID")

        button_following_user.setOnClickListener {
            unfollowUser(authorId.toString())
            authorFollowing = !authorFollowing
        }

        button_follow_user.setOnClickListener {
            followUser(authorId.toString())
            authorFollowing = !authorFollowing
        }

        seriesList.addItemDecoration(SpaceItemDecoration(10))
        seriesList.layoutManager = GridLayoutManager(context!!, 2)
        seriesList.adapter = videoAdapter

        viewModel.contentList.observe(this, Observer { video ->
            var countVideos = 0
            video.let { value ->
                videoAdapter.refreshList(value)
                repeat(value.size) {
                    countVideos++
                }
            }
            if (countVideos == 1) {
                count_videos.text = "$countVideos video"
            } else count_videos.text = "$countVideos videos"
        })

        viewModel.content.observe(this, Observer { content ->
            content.let {
                val author = it.author!!.profile!!.realName.toString()
                authorName = author
            }
        })

        viewModel.getVideos(authorId.toString())
    }

    override fun onMediaSelected(uuid: String, contentType: Int) {
        if (contentType == CONTENT_TYPE_SERIES) selectedContent = seriesContent else selectedContent = communityContent
        val intent = Intent(context!!, DetailActivity::class.java)
        intent.putExtra("UUID_INTENT", uuid)
        intent.putExtra("CONTENT_TYPE_INTENT", contentType)
        intent.putExtra(DetailActivity.SELECTED_CONTENT, selectedContent)
        intent.putExtra("SCREEN", "UserProfile")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun followUser(id: String) {
        viewModel.followById(id)
        button_following_user.visibility = View.VISIBLE
        button_follow_user.visibility = View.GONE
    }

    private fun unfollowUser(id: String) {
        viewModel.unfollowById(id)
        button_follow_user.visibility = View.VISIBLE
        button_following_user.visibility = View.GONE
    }
}
