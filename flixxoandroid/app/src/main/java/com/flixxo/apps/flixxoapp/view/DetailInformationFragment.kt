package com.flixxo.apps.flixxoapp.view

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.facebook.shimmer.ShimmerFrameLayout
import com.flixxo.apps.flixxoapp.BuildConfig
import com.flixxo.apps.flixxoapp.R
import com.flixxo.apps.flixxoapp.model.FollowState
import com.flixxo.apps.flixxoapp.utils.loadFromCustom
import com.flixxo.apps.flixxoapp.utils.timeFormat
import com.flixxo.apps.flixxoapp.viewModel.DetailViewModel
import com.masterwok.simplevlcplayer.fragments.Subtitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

interface SearchClicked {
    fun search(text: String)
}

class DetailInformationFragment : Fragment() {

    private val viewModel by sharedViewModel<DetailViewModel>(key = "detail")
    private lateinit var mCallback: SearchClicked
    private var categoryName: String = ""
    private var authorName: String = ""
    private var authorId: String = ""
    private var authorAvatar: String = ""
    private var authorNickname: String = ""
    private var state: Boolean = false

    private lateinit var durationView: LinearLayout
    private lateinit var durationIcon: ImageView
    private lateinit var durationText: TextView
    private lateinit var fullScreenBackground: LinearLayout
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mCallback = activity as SearchClicked
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.detail_information_view, container, false)

        val detailAuthor = view.findViewById<TextView>(R.id.detail_author_information)
        val information = view.findViewById<TextView>(R.id.information)
        val detailBody = view.findViewById<TextView>(R.id.detail_body_content)
        val avatar = view.findViewById<ImageView>(R.id.avatar)
        val detailTitle = view.findViewById<TextView>(R.id.detail_title)
        val shimerTitle = view.findViewById<ShimmerFrameLayout>(R.id.simmer_detail_title)
        val shimerInformation = view.findViewById<ShimmerFrameLayout>(R.id.simmer_detail_information)
        val shimmerAuthor = view.findViewById<ShimmerFrameLayout>(R.id.shimmer_detail_author)
        val shimmerBody = view.findViewById<ShimmerFrameLayout>(R.id.shimmer_detail_body)
        val shimmerSubs = view.findViewById<ShimmerFrameLayout>(R.id.shimmer_detail_subs)
        val detailSubs = view.findViewById<TextView>(R.id.detail_subs_information)

        durationView = view.findViewById(R.id.view_duration)
        durationIcon = view.findViewById(R.id.icon_duration)
        durationText = view.findViewById(R.id.text_duration)
        fullScreenBackground = view.findViewById(R.id.full_screen_information)


        listOf(shimerTitle, shimerInformation, shimmerAuthor, shimmerBody).forEach { it.startShimmer() }

        viewModel.content.observe(this, Observer {

            val detail = it.author?.profile?.realName.toString()
            val detailInformation = SpannableStringBuilder()
            val langCode = it.audioLang.toString()
            val categoryDetail = it.category?.name
            val durationDetail = it.duration?.timeFormat()
            val langDetail = viewModel.getCompleteLanguage(context!!.assets, langCode)
            val authorInformation = SpannableStringBuilder()

            durationView.visibility = View.VISIBLE
            showDurationInfo(it.duration, it.uuid)

            authorId = it.author?.id.toString()

            viewModel.followings.observe(this, Observer {
                val users = it.map { FollowState(it, true) }
                if (users.any { it.user.id == authorId }) state = true else state
            })

            authorName = detail
            authorNickname = it.author?.nickname.orEmpty()
            val stringAuthorNickname = String.format(getString(R.string.byAuthor), authorNickname)
            authorInformation.append(stringAuthorNickname)
            authorInformation.setSpan(UnderlineSpan(), 3 , authorInformation.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            authorInformation.setSpan(StyleSpan(Typeface.BOLD), 3 , authorInformation.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)


            actionShimmer(shimmerAuthor, detailAuthor)
            detailAuthor.text = authorInformation

            actionShimmer(shimerTitle, detailTitle)
            detailTitle.text = it.title.toString()

            actionShimmer(shimmerSubs, detailSubs)
            detailSubs.text = getSubtitlesInfo(it.subtitle)


            if (categoryDetail != null) {
                detailInformation.append("$categoryDetail").append("     ")
                detailInformation.setSpan(UnderlineSpan(), 0, categoryDetail.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                categoryName = categoryDetail
            }

            if (!langDetail.isEmpty()) {
                detailInformation.append(langDetail).append("     ")
            }

            if (durationDetail != null && !durationDetail.isEmpty()) {
                detailInformation.append(durationDetail)
            }

            detailInformation.setSpan(StyleSpan(Typeface.BOLD), 0, detailInformation.length ,Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            actionShimmer(shimerInformation, information)
            information.text = detailInformation

            actionShimmer(shimmerBody, detailBody)
            detailBody.text = it.body.toString()
            it.author?.profile?.avatar?.let { image ->
                avatar.loadFromCustom(url = image)
                authorAvatar = image
            } ?: run {
                avatar.setImageResource(R.drawable.ic_profile_user)
                authorAvatar = ""
            }

        })

        information.setOnClickListener {
            mCallback.search(categoryName)
        }

        detailAuthor.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("Author", authorNickname)
            intent.putExtra("Avatar", authorAvatar)
            intent.putExtra("State", state)
            intent.putExtra("ID", authorId)
            startActivity(intent)
        }


        viewModel.series.observe(this, Observer {
            val serieDetailWrapper: MutableList<SerieDetailWrapper> = mutableListOf()
            var episodeCount = 0

            for (season in it.season) {
                serieDetailWrapper.add(SerieDetailWrapper(SerieDetailRow.HEADER, season = season))
                for (episode in season.content) {
                    episodeCount++
                    serieDetailWrapper.add(SerieDetailWrapper(SerieDetailRow.ROW, content = episode))
                }
            }

            val detail = it.author.profile?.realName ?: ""
            val detailInformation = SpannableStringBuilder()
            val langCode = it.season.first().content.first().audioLang ?: "English"
            val langDetail = viewModel.getCompleteLanguage(context!!.assets, langCode)
            val authorInformation = SpannableStringBuilder()
            authorId = it.author?.id.toString()

            viewModel.followings.observe(this, Observer {
                val users = it.map { FollowState(it, true) }
                val checked = users.any { it.user.id == authorId }
                state = checked
            })

            val categoryDetail = it.category?.name
            authorName = detail
            authorNickname = it.author!!.nickname!!
            val stringAuthorNickname = String.format(getString(R.string.byAuthor), authorNickname)
            authorInformation.append(stringAuthorNickname)
            authorInformation.setSpan(UnderlineSpan(), 3 , authorInformation.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            authorInformation.setSpan(StyleSpan(Typeface.BOLD), 3 , authorInformation.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

            actionShimmer(shimmerAuthor, detailAuthor)
            detailAuthor.text = authorInformation

            actionShimmer(shimerTitle, detailTitle)
            detailTitle.text = it.title.toString()

            actionShimmer(shimmerSubs, detailSubs)
            detailSubs.text = getSubtitlesInfo(it.season.first().content.first().subtitle)


            if (categoryDetail != null) {
                detailInformation.append(categoryDetail).append("     ")
                detailInformation.setSpan(UnderlineSpan(), 0, categoryDetail.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                categoryName = categoryDetail
            }

            if (!langDetail.isEmpty()) {
                detailInformation.append(langDetail).append("     ")
            }

            if (episodeCount > 0 && it.uuid != BuildConfig.INTERACTIVE_SERIE_UUID) {
                detailInformation.append("$episodeCount episodes")
            }

            detailInformation.setSpan(StyleSpan(Typeface.BOLD), 0, detailInformation.length ,Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            actionShimmer(shimerInformation, information)
            information.text = detailInformation

            actionShimmer(shimmerBody, detailBody)
            detailBody.text = it.body.toString()

            it.author?.profile?.avatar?.let { image ->
                avatar.loadFromCustom(url = image)
                authorAvatar = image
            } ?: run {
                avatar.setBackgroundResource(R.drawable.ic_profile_user)
                authorAvatar = ""
            }

            val durationSerie = it.season.first().content.first().duration
            showDurationInfo(durationSerie, it.uuid)

        })

        return view

    }

    private fun showDurationInfo(duration: Long?, uuid: String?) {

        if (duration == null) {
            return
        }
        durationView.visibility = View.VISIBLE
        when {
            duration!! in 0..300 -> {
                //fullScreenBackground.setBackgroundResource(R.drawable.ic_backveryshort)
                durationIcon.setImageResource(R.drawable.ic_very_short)
                durationText.setText(R.string.very_short)
            }
            duration in 301..599 -> {
                //fullScreenBackground.setBackgroundResource(R.drawable.ic_backshort)
                durationIcon.setImageResource(R.drawable.ic_iconshort)
                durationText.setText(R.string.short_text)
            }
            else -> {
                //fullScreenBackground.setBackgroundResource(R.drawable.ic_backnotsoshort)
                durationIcon.setImageResource(R.drawable.ic_not_so_short)
                durationText.setText(R.string.not_so_short)
            }
        }
        //show interactive duration in interactive serie
        if(uuid == BuildConfig.INTERACTIVE_SERIE_UUID) {
            fullScreenBackground.setBackgroundResource(R.color.flixxoBackgorundColor)
            durationIcon.setImageResource(R.drawable.icon_interactive)
            durationText.text = getString(R.string.interactive)
        }
    }

    private fun actionShimmer(shimmerFrameLayout: ShimmerFrameLayout, view: View) {
        shimmerFrameLayout.visibility = View.GONE
        shimmerFrameLayout.stopShimmer()
        view.visibility = View.VISIBLE
    }

    private fun getSubtitlesInfo(subtitle: ArrayList<Subtitle>?): StringBuilder {
        val subsInformation = StringBuilder()
        subtitle?.forEach { subs ->
            val subComplete = viewModel.getCompleteLanguage(context!!.assets, subs.lang!!)
            when (subtitle.size) {
                1 -> subsInformation.append("$subComplete ")
                else -> subsInformation.append(subComplete).append("-")
            }
        }
        if (subtitle?.isEmpty()!!) {
            subsInformation.append("None ")
        }
        subsInformation.deleteCharAt(subsInformation.length - 1)
        return subsInformation
    }

}