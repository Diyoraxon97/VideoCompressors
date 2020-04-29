package com.example.demovideocompressors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.format.DateUtils
import android.view.View
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.bumptech.glide.GenericTransitionOptions.with
import com.bumptech.glide.Glide.with
import kotlinx.android.synthetic.main.activity_main3.*
import java.io.File
import java.io.IOException

class Main3Activity : AppCompatActivity() {

    companion object {
        const val REQUEST_SELECT_VIDEO = 0
    }

    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        chooseVideo.setOnClickListener {
            checkPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) { pickVideo() }

        }
        buttonPlay.setOnClickListener { VideoPlayActivity.start(this, path) }
    }

    //Pick a video file from device
    private fun pickVideo() {
        val intent = Intent()
        intent.apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(Intent.createChooser(intent, "Select video"), REQUEST_SELECT_VIDEO)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == REQUEST_SELECT_VIDEO) {
                if (data != null && data.data != null) {
                    val uri = data.data
                    path = getMediaPath(this, uri!!)
                    val file = File(path)
                    videoImage.load(file)
                    val downloadsPath =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val desFile = File(downloadsPath, "${System.currentTimeMillis()}_${file.name}")
                    if (desFile.exists()) {
                        desFile.delete()
                        try {
                            desFile.createNewFile()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                    var time = 0L
                    VideoCompressor.doVideoCompression(
                        path,
                        desFile.path,
                        object : CompressionListener {
                            override fun onProgress(percent: Float) {
                                //Update UI
                                textPercent.text = "${percent.toLong()}%"
                            }

                            override fun onStart() {
                                time = System.currentTimeMillis()
                                textSizeOriginal.text =
                                    "Original size: ${getFileSize(file.length())}"
                                textNewSize.text = ""
                                textTime.text = ""
                            }

                            override fun onSuccess() {
                                val newSizeValue = desFile.length()
                                textNewSize.text =
                                    "Size after compression: ${getFileSize(newSizeValue)}"
                                textPercent.text = ""
                                time = System.currentTimeMillis() - time
                                textTime.text =
                                    "Duration: ${DateUtils.formatElapsedTime(time / 1000)}"

                                path = desFile.path
                            }

                            override fun onFailure() {
                                textPercent.text = "This video cannot be compressed!"
                            }
                        })
                }
            }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
