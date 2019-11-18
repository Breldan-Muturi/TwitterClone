package turi.practice.twitterclone.listeners

import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import turi.practice.twitterclone.util.*

class TwitterListenerimpl(
    val tweetList: RecyclerView,
    var user: User?,
    val callback: HomeCallback?
) : TweetListener {
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onLayoutClicked(tweet: Tweet?) {
        tweet?.let {
            val owner = tweet?.userIds?.get(0)
            if (owner != userId) {
                if (user?.followUsers?.contains(owner) == true) {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("Unfollow ${tweet.username}?")
                        .setPositiveButton("Yes") { dialog, which ->
                            tweetList.isClickable = false
                            var followedUsers = user?.followUsers
                            if(followedUsers == null){
                                followedUsers = arrayListOf()
                            }
                            followedUsers?.remove(owner)
                            firebaseDB.collection(DATA_USERS).document(userId!!).update(
                                DATA_USER_FOLLOW, followedUsers
                            )
                                .addOnSuccessListener {
                                    tweetList.isClickable = true
                                    callback?.onRefresh()
                                }
                                .addOnFailureListener {
                                    tweetList.isClickable = true
                                }
                        }
                        .setNegativeButton("Cancel") { dialog, which -> }
                        .show()
                } else {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("Follow ${tweet.username}?")
                        .setPositiveButton("Yes") { dialog, which ->
                            tweetList.isClickable = false
                            var followedUsers = user?.followUsers
                            if(followedUsers == null){
                                followedUsers = arrayListOf()
                            }
                            owner?.let {
                                followedUsers?.add(owner)
                                firebaseDB.collection(DATA_USERS).document(userId!!).update(
                                    DATA_USER_FOLLOW, followedUsers
                                )
                                    .addOnSuccessListener {
                                        tweetList.isClickable = true
                                        callback?.onUserUpdated()
                                    }
                                    .addOnFailureListener {
                                        tweetList.isClickable = true
                                    }
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, which -> }
                        .show()
                }
            }
        }
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
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!)
                .update(DATA_TWEETS_LIKES, likes)
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
        tweet?.let {
            tweetList.isClickable = false
            val retweets = tweet.userIds
            if (retweets?.contains(userId) == true) {
                retweets.remove(userId)
            } else {
                retweets?.add(userId!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!)
                .update(DATA_TWEETS_USER_IDS, retweets)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    tweetList.isClickable = true
                }
        }
    }
}