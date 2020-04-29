package com.example.demovideocompressors

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.io.File
import java.lang.Math.log10
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

fun getFileSize(size: Long): String {
    if (size <= 0)
        return "0"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}
fun getMediaPath(context: Context, uri: Uri): String {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    var cursor: Cursor? = null
    try {
        cursor = context.contentResolver.query(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)

        } else ""
    }finally {
        cursor?.close()
    }

}
fun Long.toDatetime(pattern: String = "yyyy-MM-dd hh:mm:ss") = SimpleDateFormat(pattern).format(Date(this))
fun ImageView.load(url: String?) = Glide.with(context).load(url).placeholder(R.drawable.ic_image_black_24dp).into(this)
fun ImageView.load(file: File) = Glide.with(context).load(file).placeholder(R.drawable.ic_image_black_24dp).into(this)
fun Activity.checkPermissions(permissions: Array<String>, granted: () -> Unit) {
    val mContext = this.applicationContext ?: return
    val options = Permissions.Options()
    options.setCreateNewTask(true)
    Permissions.check(mContext, permissions, null, options, object : PermissionHandler() {
        override fun onGranted() {
            granted()
        }
    })
}