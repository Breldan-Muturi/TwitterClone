package turi.practice.twitterclone.listeners

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import turi.practice.twitterclone.util.DATA_TWEETS
import turi.practice.twitterclone.util.DATA_TWEETS_LIKES
import turi.practice.twitterclone.util.Tweet
import turi.practice.twitterclone.util.User

class TwitterListenerimpl(
    val tweetList: RecyclerView,
    var user: User?,
    val callback: HomeCallback?
) : TweetListener {
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    override fun onLayoutClicked(tweet: Tweet?) {

    }

    override fun onLike(tweet: Tweet?) {
        tweet?.let {
            tweetList.isClickable = false
            val likes = tweet.likes
            if (tweet.likes?.contains(userId) == true) {
                likes?.remove(userId)
            } else {
                likes?.add(userId!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!).update(DATA_TWEETS_LIKES, likes)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    tweetList.isClickable = true
                }
        }
    }

    override fun onRetweet(tweet: Tweet?) {
    }
}