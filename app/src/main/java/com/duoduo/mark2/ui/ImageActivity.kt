package com.duoduo.mark2.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.duoduo.mark2.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class ImageActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var url: String
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        supportActionBar?.hide()

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        var systemUiVisibility: Int = window.decorView.getSystemUiVisibility()
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.setSystemUiVisibility(systemUiVisibility)

        url = intent.getStringExtra("URL") ?: ""

        photoView = findViewById(R.id.photoView)
        photoView.enable()
        Glide
            .with(this)
            .load(url)
            .into(photoView)
        photoView.setOnClickListener {
            finish()
        }
        photoView.setOnLongClickListener {
            MaterialAlertDialogBuilder(this)
                .setItems(
                    arrayOf("保存图片")
                ) { _, i ->
                    saveImage()
                }
                .show()
            false
        }
    }

    fun saveImage() {
        toast = Toast.makeText(this, "保存中...", Toast.LENGTH_SHORT)
        toast!!.show()

        Glide.with(this)
            .load(url)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    try {
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MDClubAndroid")
                        file!!.mkdirs()

                        val os = FileOutputStream(File(file, System.currentTimeMillis().toString() + ".jpeg"))
                        (resource as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                        os.flush()
                        os.close()

                        toast?.cancel()
                        toast = Toast.makeText(this@ImageActivity, "保存成功: ${file.absolutePath}", Toast.LENGTH_SHORT)
                        toast!!.show()

                    } catch (e: Exception) {
                        Log.e("ImageActivity", "save", e)
                        toast?.cancel()
                        toast = Toast.makeText(this@ImageActivity, e.message, Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

}