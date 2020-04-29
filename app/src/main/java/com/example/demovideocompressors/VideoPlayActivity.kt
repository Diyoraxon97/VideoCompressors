package com.example.demovideocompressors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : AppCompatActivity() {
    companion object {
        fun start(activity: Activity, uri: String) {
            val intent = Intent(activity, VideoPlayActivity::class.java)
            intent.putExtra("uri", uri)
            activity.startActivity(intent)
        }
    }
    private lateinit var filePath:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        if (intent != null) {
            val bundle: Bundle? = intent.extras
            filePath = bundle?.getString("uri", "")?:""
        }

        playVideo(filePath)
        buttonPlay.setOnClickListener {
            playVideo(filePath);
            it.visibility = View.GONE;
            playPauseButton.setImageResource(R.drawable.ic_pause_black)
            playPauseButton.tag = 0
        }
        videoView.setOnCompletionListener {
            stop()
            videoView.seekTo(0)
            seekBar.progress = 0
        }
        playPauseButton.setOnClickListener {
            if (it.tag == 0) {
                if (videoView.canPause()) {
                    videoView.pause()
                    stop()
                }
            } else {
                videoView.start()
                play()
            }
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) videoView.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(updateTimeTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    handler.removeCallbacks((updateTimeTask))
                    videoView.seekTo(it.progress)
                    updateProgressBar()
                }
            }

        })
    }

    private val handler = Handler()

    override fun onStop() {
        super.onStop()
        realisePlayer()
    }

    fun play() {
        buttonPlay.visibility = View.GONE
        playPauseButton.setImageResource(R.drawable.ic_pause_black)
        playPauseButton.tag = 0
    }

    fun stop() {
        playPauseButton.tag = 1
        playPauseButton.setImageResource(R.drawable.ic_play_circle_outline_black_24dp)
        buttonPlay.visibility = View.VISIBLE
    }

    private fun playVideo(filePath: String) {
        try {
            videoView.setVideoURI(Uri.parse(filePath));
            //  videoView.setV
            videoView.setOnPreparedListener { mp ->
                // videoView.setBackgroundResource(R.color.colorTransparent);
                videoView.start();
                updateProgressBar()
                play()
            }
            videoView.requestFocus();
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun realisePlayer() {
        videoView.stopPlayback()
    }

    private fun updateProgressBar() {
        handler.postDelayed(updateTimeTask, 100)
    }

    private val updateTimeTask: Runnable = object : Runnable {
        override fun run() {
            seekBar?.progress = videoView.currentPosition
            seekBar?.max = videoView.duration
            seekBar?.postDelayed(this, 100)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause()
        }
    }
}
