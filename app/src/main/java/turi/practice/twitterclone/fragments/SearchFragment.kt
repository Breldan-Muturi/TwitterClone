package turi.practice.twitterclone.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search.*

import turi.practice.twitterclone.R
import turi.practice.twitterclone.adapters.TweetListAdapter
import turi.practice.twitterclone.listeners.TweetListener
import turi.practice.twitterclone.listeners.TwitterListenerimpl
import turi.practice.twitterclone.util.*

class SearchFragment : TwitterFragment() {
    private var currentHashtag = ""
    private var hashTagFollowed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = TwitterListenerimpl(tweetList, currentUser, callback)
        tweetsAdapter = TweetListAdapter(userId!!, arrayListOf())
        tweetsAdapter?.setListener(listener)
        tweetList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tweetsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            updateList()
        }
        followHashtag.setOnClickListener {
            followHashtag.isClickable = false
            val followed = currentUser?.followHashtags
            if (hashTagFollowed) {
                followed?.remove(currentHashtag)
            } else {
                followed?.add(currentHashtag)
            }
            firebaseDB.collection(DATA_USERS).document(userId).update(DATA_USER_HASHTAGS, followed)
                .addOnSuccessListener {
                    callback?.onUserUpdated()
                    followHashtag.isClickable = true
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    followHashtag.isClickable = true
                }
        }
    }

    fun newHashtag(term: String) {
        currentHashtag = term
        followHashtag.visibility = View.VISIBLE

        updateList()
    }

    override fun updateList() {
        tweetList.visibility = View.GONE
        firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEETS_HASHTAGS, currentHashtag)
            .get()
            .addOnSuccessListener { list ->
                tweetList.visibility = View.VISIBLE
                val tweets = arrayListOf<Tweet>()
                for (document in list.documents) {
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.let { tweets.add(it) }
                }
                val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
                tweetsAdapter?.updateTweets(sortedTweets)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
        updateFollowDrawable()
    }

    private fun updateFollowDrawable(){
        hashTagFollowed = currentUser?.followHashtags?.contains(currentHashtag) == true
        context?.let {
            if (hashTagFollowed) {
                followHashtag.setImageDrawable(
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.follow
                    )
                )
            } else{
                followHashtag.setImageDrawable(
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.follow_inactive
                    )
                )
            }
        }
    }
}
