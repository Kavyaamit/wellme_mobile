package com.wellme


import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.wellme.utils.UtilMethod
import java.util.regex.Matcher
import java.util.regex.Pattern


class VideoActivity : FragmentActivity(), View.OnClickListener{
    var bun : Bundle? = null
    var url1 : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 27) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }

        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.setStatusBarColor(this.resources.getColor(R.color.colorPrimary))
        }
        setContentView(R.layout.fragment_video)

        bun = intent.extras
        if (bun != null) {
            if (bun!!.containsKey("url")) {
                url1 = bun!!.getString("url")
                //url = "https://www.youtube.com/channel/UCQ9Bo4h8YN-JVP66y49dVDQ";
                val mYoutubePlayerFragment =
                    YouTubePlayerSupportFragment()
                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.youtube_fragment1, mYoutubePlayerFragment)
                fragmentTransaction.commit()
                mYoutubePlayerFragment.initialize(
                    "AIzaSyBjXn5c1N7zihpJW-DSN3XGsN-tQOHr97A",
                    object : YouTubePlayer.OnInitializedListener {
                        override fun onInitializationSuccess(
                            provider: YouTubePlayer.Provider,
                            youTubePlayer: YouTubePlayer,
                            b: Boolean
                        ) {
                            if (!b) {
                                try {
                                    val id = UtilMethod.instance.getVideoIdFromYoutubeUrl(url1)
                                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                                    youTubePlayer.setFullscreen(true)
                                    youTubePlayer.loadVideo(id)
                                    youTubePlayer.play()
                                    youTubePlayer.setPlaybackEventListener(object :
                                        PlaybackEventListener {
                                        override fun onPlaying() {}
                                        override fun onPaused() {}
                                        override fun onStopped() {}
                                        override fun onBuffering(b: Boolean) {}
                                        override fun onSeekTo(i: Int) {}
                                    })
                                    youTubePlayer.setPlayerStateChangeListener(object :
                                        PlayerStateChangeListener {
                                        override fun onLoading() {}
                                        override fun onLoaded(s: String) {}
                                        override fun onAdStarted() {}
                                        override fun onVideoStarted() {}
                                        override fun onVideoEnded() {}
                                        override fun onError(errorReason: YouTubePlayer.ErrorReason) {}
                                    })
                                    youTubePlayer.play()
                                } catch (e: Exception) {
                                    Log.v("Exception on success", " $e")
                                }
                            }
                        }

                        override fun onInitializationFailure(
                            provider: YouTubePlayer.Provider,
                            youTubeInitializationResult: YouTubeInitializationResult
                        ) {
                        }
                    })
            }
        }


    }


    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}