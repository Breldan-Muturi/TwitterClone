package turi.practice.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_tweet.*
import turi.practice.twitterclone.R
import turi.practice.twitterclone.util.DATA_TWEETS
import turi.practice.twitterclone.util.Tweet

class TweetActivity : AppCompatActivity() {
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val imageUrl: String? = null
    private var userId: String? = null
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet)
        if (intent.hasExtra(PARAM_USER_ID) && intent.hasExtra(PARAM_USER_USERNAME)) {
            userId = intent.getStringExtra(PARAM_USER_ID)
            userName = intent.getStringExtra(PARAM_USER_USERNAME)
        } else {
            Toast.makeText(this, "Error creating tweet", Toast.LENGTH_LONG).show()
            finish()
        }
        tweetProgressLayout.setOnTouchListener { v, event -> true }
    }

    fun addImage(v: View) {

    }

    fun postTweet(v: View) {
        tweetProgressLayout.visibility = View.VISIBLE
        val text = tweetText.text.toString()
        val hashtags = getHashtags(text)
        val tweetId = firebaseDB.collection(DATA_TWEETS).document()
        val tweet = Tweet(
            tweetId.id,
            arrayListOf(userId!!),
            userName,
            text,
            imageUrl,
            System.currentTimeMillis(),
            hashtags,
            arrayListOf()
        )
        tweetId.set(tweet)
            .addOnCompleteListener { finish() }
            .addOnFailureListener { e ->
                e.printStackTrace()
                tweetProgressLayout.visibility = View.GONE
                Toast.makeText(this, "Failed to post the tweet. Please try again.", Toast.LENGTH_LONG).show()
            }
    }

    fun getHashtags(source: String): ArrayList<String> {
        val hashtags = arrayListOf<String>()
        var text = source
        while(text.contains("#")){
            var hashtag = ""
            val hash = text.indexOf("#")
            text = text.substring(hash + 1)
            val firstSpace = text.indexOf(" ")
            val firstHash = text.indexOf("#")
            if(firstSpace == -1 && firstHash == -1){
                hashtag = text.substring(0)
            } else if (firstSpace != -1 && firstSpace < firstHash){
                hashtag = text.substring(0, firstSpace)
                text = text.substring(firstSpace + 1)
            } else {
                hashtag = text.substring(0, firstHash)
                text = text.substring(firstHash )
            }
            if(!hashtag.isNullOrEmpty()){
                hashtags.add(hashtag)
            }
        }
        return hashtags
    }

    companion object {
        val PARAM_USER_ID = "UserId"
        val PARAM_USER_USERNAME = "UserName"
        fun newIntent(context: Context, userId: String?, userName: String?): Intent {
            val intent = Intent(context, TweetActivity::class.java)
            intent.putExtra(PARAM_USER_ID, userId)
            intent.putExtra(PARAM_USER_USERNAME, userName)
            return intent
        }
    }

}
