package turi.practice.twitterclone.listeners

import turi.practice.twitterclone.util.Tweet

interface TweetListener {
    fun onLayoutClicked(tweet: Tweet?)
    fun onLike(tweet: Tweet?)
    fun onRetweet(tweet: Tweet?)
}