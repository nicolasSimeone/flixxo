package com.flixxo.apps.flixxoapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.flixxo.apps.flixxoapp.BuildConfig
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.FileHelper
import com.flixxo.apps.flixxoapp.utils.formatValue
import com.flixxo.apps.flixxoapp.utils.loadFromCustom
import com.flixxo.apps.flixxoapp.viewModel.UserProfileViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import de.hdodenhof.circleimageview.CircleImageView
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment() {

    private val viewModel: UserProfileViewModel by viewModel()

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var bundle: Bundle


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        bundle = Bundle()
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

        firebaseAnalytics.logEvent("profile_screen", bundle)

        val name = view.findViewById<TextView>(R.id.name_profile)
        name.text = viewModel.getUsername().trim()

        viewModel.user.observe(this, Observer {
            name.text = it.profile.realName

            val avatar = view.findViewById<CircleImageView>(R.id.avatar_profile)

            if (it.profile.avatar?.toString().isNullOrEmpty()) it.profile.avatar = null

            it.profile.avatar?.let { url ->
                avatar.loadFromCustom(url)
            } ?: run {
                avatar.setBackgroundResource(R.drawable.ic_profile_user)
            }
        })

        val followers = view.findViewById<LinearLayout>(R.id.shape_followers)
        val followersText = view.findViewById<TextView>(R.id.number_followers)

        viewModel.followers.observe(this, Observer { users ->
            followersText.text = users.count().toString()
        })

        val followings = view.findViewById<LinearLayout>(R.id.shape_followings)
        val followingsText = view.findViewById<TextView>(R.id.number_followings)

        viewModel.followings.observe(this, Observer { users ->
            followingsText.text = users.count().toString()
        })

        followers.setOnClickListener {
            val bundle = bundleOf("FollowList" to viewModel.followersStateList(), "Type" to "Followers")
            view.findNavController().navigate(R.id.followFragment, bundle)
        }

        followings.setOnClickListener {
            val bundle = bundleOf("FollowList" to viewModel.followingStateList(), "Type" to "Followings")
            view.findNavController().navigate(R.id.followFragment, bundle)
        }

        val balance = view.findViewById<TextView>(R.id.balance)

        viewModel.balance.observe(this, Observer {
            balance.text = it.amount.formatValue()
        })

        val logOut = view.findViewById<TextView>(R.id.log_out)
        logOut.setOnClickListener {

            val alert = AlertDialog.Builder(context!!, R.style.CustomDialog)
            alert.setTitle(getString(R.string.areyousureCaps))
            alert.setPositiveButton(getString(R.string.logoutLower)) { _, _ ->
                PreferencesManager.getInstance(activity!!).clearKey("USER_SECRET")
                logoutFromFacebook()
                val intent = Intent(activity!!, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity!!.startActivity(intent)
            }
            alert.setNegativeButton(getString(R.string.cancelLower), null)
            alert.setCancelable(false)
            alert.show()
        }

//        val withdrawal = view.findViewById<TextView>(R.id.withdrawal)
//        withdrawal.setOnClickListener {
//            val intent = Intent(activity, WithdrawalsActivity::class.java)
//            activity!!.startActivity(intent)
//        }

        val deposit = view.findViewById<TextView>(R.id.deposit)
        deposit.setOnClickListener {
            view.findNavController().navigate(R.id.depositFragment)
        }

        val terms = view.findViewById<TextView>(R.id.t_and_c)
        terms.setOnClickListener {
            val intent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://www.flixxo.com/terms.html")
            )
            startActivity(intent)
        }

        val privacy = view.findViewById<TextView>(R.id.p_and_p)
        privacy.setOnClickListener {
            val intent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://flixxo.com/privacy-policy.html")
            )
            startActivity(intent)
        }

        val edit = view.findViewById<TextView>(R.id.edit_profile)
        edit.setOnClickListener {
            view.findNavController().navigate(R.id.editProfileFragment)
        }

        val account = view.findViewById<TextView>(R.id.account)
        account.setOnClickListener {
            view.findNavController().navigate(R.id.accountFragment)
        }

        val deleteCache = view.findViewById<TextView>(R.id.delete_cache)
        deleteCache.setOnClickListener {
            val alert = AlertDialog.Builder(context!!, R.style.CustomDialog)
            alert.setTitle(getString(R.string.areyousureCaps))
            alert.setPositiveButton(getString(R.string.delete)) { _, _ ->
                FileHelper.deleteTorrentFolder(context!!)
            }
            alert.setNegativeButton(getString(R.string.cancelLower), null)
            alert.setCancelable(false)
            alert.show()
        }

        val versionNumber = view.findViewById<TextView>(R.id.versionNumber)
        versionNumber.text = String.format(getString(R.string.versionNumber), BuildConfig.VERSION_CODE)

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBalance()
        viewModel.getFollowers()
        viewModel.getFollowings()
        viewModel.getProfile()
    }

    private fun logoutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return
        }
        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE,
            GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
    }
}


