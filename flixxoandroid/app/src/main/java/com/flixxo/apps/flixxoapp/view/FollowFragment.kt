package com.flixxo.apps.flixxoapp.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.Author
import com.flixxo.apps.flixxoapp.model.FollowState
import com.flixxo.apps.flixxoapp.viewModel.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_following.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FollowFragment : Fragment(), OnFollowSelect {
    private lateinit var adapterFollow: FollowAdapter
    private val viewModel: UserProfileViewModel by viewModel()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val followList: MutableList<Author> = mutableListOf()
    private lateinit var userList: List<FollowState>
    private lateinit var type: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userList = arguments?.get("FollowList") as ArrayList<FollowState>
        type = arguments?.get("Type") as String
        linearLayoutManager = LinearLayoutManager(context!!)
        recycler_view_followers.layoutManager = linearLayoutManager
        adapterFollow = FollowAdapter(userList, onFollowSelect = this)
        recycler_view_followers.adapter = adapterFollow

        search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                clean_search.visibility = View.VISIBLE
                adapterFollow.filter.filter(search.text.toString())
            }
            return@setOnKeyListener false
        }

        clean_search.setOnClickListener {
            search.text.clear()
            adapterFollow.refreshList(userList)
            clean_search.visibility = View.GONE
        }

        viewModel.followersUpdated.observe(this, Observer {
            userList = it
            adapterFollow.refreshList(userList)
        })

        viewModel.followingsUpdated.observe(this, Observer {
            userList = it
            adapterFollow.refreshList(userList)
        })
    }

    override fun userSelected(user: FollowState, pos: Int) {
        val avatar = user.user.profile?.avatar ?: ""
        val bundle = bundleOf(
            "Author" to user.user.nickname,
            "Avatar" to avatar,
            "State" to user.state,
            "ID" to user.user.id
        )
        view?.findNavController()?.navigate(R.id.userProfileFragment, bundle)
    }

    override fun isFollowing(user: Author): Boolean {
        return followList.contains(user)
    }

    override fun followUser(id: String, pos: Int) {
        viewModel.followById(id)
        userList[pos].state = !userList[pos].state
    }

    override fun unfollowUser(id: String, pos: Int) {
        viewModel.unfollowById(id)
        userList[pos].state = !userList[pos].state
    }

    override fun onResume() {
        super.onResume()
        if (type == "Followers") viewModel.followersUserUpdated() else viewModel.followingsUserUpdated()
    }
}