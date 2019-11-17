package turi.practice.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import turi.practice.twitterclone.R
import turi.practice.twitterclone.fragments.HomeFragment
import turi.practice.twitterclone.fragments.MyActivityFragment
import turi.practice.twitterclone.fragments.SearchFragment
import turi.practice.twitterclone.util.DATA_USERS
import turi.practice.twitterclone.util.User
import turi.practice.twitterclone.util.loadUrl

class HomeActivity : AppCompatActivity() {
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()

    private var sectionsPagerAdapter: SectionPagerAdapter? = null
    private var userId = FirebaseAuth.getInstance().currentUser?.uid
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sectionsPagerAdapter = SectionPagerAdapter(supportFragmentManager)

        //Link container of layout with the adapter
        container.adapter = sectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
            }
        })

        logo.setOnClickListener{view ->
            startActivity(ProfileActivity.newIntent(this))
        }

        fab.setOnClickListener{
            startActivity(TweetActivity.newIntent(this, userId, user?.username))
        }
        homeProgressLayout.setOnTouchListener{ v, event -> true }
        search.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFragment.newHashtag(v?.text.toString())
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId == null){
            startActivity(LoginActivity.newIntent(this))
            finish()
        }
        populate()
    }

    fun populate(){
        homeProgressLayout.visibility = View.VISIBLE
        firebaseDB.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                homeProgressLayout.visibility = View.GONE
                user = documentSnapshot.toObject(User::class.java)
                user?.imageUrl?.let {
                    logo.loadUrl(it, R.drawable.logo)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }
    inner class SectionPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm){
        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> homeFragment
                1 -> searchFragment
                else -> myActivityFragment
            }
        }

        override fun getCount() = 3
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}
