package com.flixxo.apps.flixxoapp.view

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.FollowState
import com.flixxo.apps.flixxoapp.utils.inflate
import com.flixxo.apps.flixxoapp.utils.loadFromCustom
import kotlinx.android.synthetic.main.custom_list_followings.view.*

class FollowAdapter(private var follow: List<FollowState> = ArrayList(), private var onFollowSelect: OnFollowSelect) :
    RecyclerView.Adapter<FollowAdapter.FollowListHolder>(), Filterable {

    private var followList: List<FollowState>? = null

    init {
        this.followList = follow.sortedWith(compareBy { it.user.nickname })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListHolder {
        val inflatedView = parent.inflate(R.layout.custom_list_followings)
        return FollowListHolder(inflatedView, onFollowSelect)
    }

    override fun onBindViewHolder(holder: FollowAdapter.FollowListHolder, position: Int) {
        val follow = followList!![position]
        holder.bind(follow)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    followList = follow
                } else {

                    val filteredList = ArrayList<FollowState>()

                    for (row in follow) {
                        val userName = row.user.profile!!.realName
                        if (userName!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }

                    followList = filteredList
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = followList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                followList = filterResults.values as ArrayList<FollowState>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = followList!!.size

    fun refreshList(follow: List<FollowState>) {
        this.followList = follow.sortedWith(compareBy { it.user.nickname })
        this.follow = follow.sortedWith(compareBy { it.user.nickname })
        notifyDataSetChanged()
    }

    class FollowListHolder(private val view: View, private val onFollowSelect: OnFollowSelect) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var follow: FollowState
        private var followList: List<FollowState>? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            for (item in this.followList!!) {
                onFollowSelect.userSelected(follow, adapterPosition)
            }
        }

        fun bind(follow: FollowState) {
            this.follow = follow
            this.followList = listOf(follow)

            val username = follow.user.nickname

            follow.user.profile?.let {
                view.user_follow.text = username

                it.avatar?.let { url ->
                    view.avatar_follow.loadFromCustom(url)
                } ?: run {
                    view.avatar_follow.setBackgroundResource(R.drawable.ic_profile_user)
                }
            } ?: run {
                view.user_follow.text = ""
            }

            if (follow.state) {
                view.button_following.visibility = View.VISIBLE
                view.button_follow.visibility = View.INVISIBLE
            } else {
                view.button_follow.visibility = View.VISIBLE
                view.button_following.visibility = View.INVISIBLE
            }

            view.button_follow.setOnClickListener {
                for (item in followList!!) {
                    onFollowSelect.followUser(follow.user.id!!, adapterPosition)
                }
                view.button_following.visibility = View.VISIBLE
                view.button_follow.visibility = View.INVISIBLE
            }

            view.button_following.setOnClickListener {
                for (item in followList!!) {
                    onFollowSelect.unfollowUser(follow.user.id!!, adapterPosition)
                }
                view.button_follow.visibility = View.VISIBLE
                view.button_following.visibility = View.INVISIBLE
            }
        }
    }
}