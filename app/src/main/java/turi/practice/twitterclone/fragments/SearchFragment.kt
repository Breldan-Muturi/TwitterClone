package turi.practice.twitterclone.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import turi.practice.twitterclone.R

class SearchFragment : TwitterFragment() {
    private var currentHashtag = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    fun newHashtag(term: String){
        currentHashtag = term
    }
}
