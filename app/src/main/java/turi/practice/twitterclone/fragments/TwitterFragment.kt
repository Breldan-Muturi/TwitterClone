package turi.practice.twitterclone.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import turi.practice.twitterclone.adapters.TweetListAdapter
import turi.practice.twitterclone.listeners.HomeCallback
import turi.practice.twitterclone.listeners.TweetListener
import turi.practice.twitterclone.util.User
import java.lang.RuntimeException

abstract class TwitterFragment: Fragment() {

    protected var tweetsAdapter: TweetListAdapter? = null
    protected var currentUser: User? = null
    protected val firebaseDB = FirebaseFirestore.getInstance()
    protected val userId = FirebaseAuth.getInstance().currentUser?.uid
    protected val listener: TweetListener? = null
    protected var callback: HomeCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeCallback){
            callback = context
        } else {
            throw RuntimeException(context.toString() + "MUST IMPLEMENT HOMECALLBACK")
        }
    }

    fun setUser(user: User?){
        this.currentUser = user
    }

    abstract fun updateList()

    override fun onResume() {
        super.onResume()
        updateList()
    }
}