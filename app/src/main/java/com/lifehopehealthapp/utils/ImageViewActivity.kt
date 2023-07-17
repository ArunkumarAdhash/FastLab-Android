package com.lifehopehealthapp.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideApp
import com.lifehopehealthapp.R
import com.lifehopehealthapp.utils.Constants.ImageURL
import jp.wasabeef.glide.transformations.BlurTransformation


class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        val bigImageView: PhotoView = findViewById(R.id.mBigImage)
        val backPress: AppCompatImageView = findViewById(R.id.imageViewBackArrow)
        val imageViewPostImageBG: AppCompatImageView = findViewById(R.id.imageViewPostImageBG)



        if (intent != null) {
            GlideApp.with(this)
                .load(intent.getStringExtra(ImageURL)!!)
                .error(R.drawable.image_placeholder)
                .into(bigImageView)


         /*   Glide.with(this)
                .load(intent.getStringExtra(ImageURL)!!)
                .placeholder(R.drawable.image_placeholder).apply(
                    RequestOptions.bitmapTransform(
                        BlurTransformation(10, 3)
                    )
                )
                .into(imageViewPostImageBG)*/
        }
        backPress.setOnClickListener {
            finish()
        }

    }




}