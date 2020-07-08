package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.utils.loadFromCustom
import com.flixxo.apps.flixxoapp.viewModel.UserProfileViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.custom_header.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class UserProfileActivity : AppCompatActivity(), OnItemSelected {

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
    private var selectedContent: String = ""
    private lateinit var header: CustomHeader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        videoAdapter = VideosAdapter(this)
        header = findViewById(R.id.custom_header)
        header.visibility = View.VISIBLE
        header.findViewById<TextView>(R.id.title_header).text = getString(R.string.profile)

        val authorProfile = intent.extras.get("Author")
        var authorAvatar = intent.extras.get("Avatar")
        var authorFollowing: Boolean = intent.extras.get("State") as Boolean

        if (authorAvatar?.toString().isNullOrEmpty()) authorAvatar = null

        authorAvatar?.let { url ->
            avatar_user_profile.loadFromCustom(url.toString())
        } ?: run {
            avatar_user_profile.setBackgroundResource(R.drawable.ic_profile_user)
        }

        authorProfile?.let { name ->
            user_profile_name.text = name.toString()
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

        val authorId = intent.extras.get("ID")

        button_following_user.setOnClickListener {
            unfollowUser(authorId.toString())
            authorFollowing = !authorFollowing
        }

        button_follow_user.setOnClickListener {
            followUser(authorId.toString())
            authorFollowing = !authorFollowing
        }

        seriesList.addItemDecoration(SpaceItemDecoration(10))
        seriesList.layoutManager = GridLayoutManager(this, 2)
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

        back_button_header.setOnClickListener {
            onBackPressed()
        }

        viewModel.getVideos(authorId.toString())
    }

    override fun onMediaSelected(uuid: String, contentType: Int) {
        if (contentType == CONTENT_TYPE_SERIES) selectedContent = seriesContent else selectedContent = communityContent
        val intent = Intent(this, DetailActivity::class.java)
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
