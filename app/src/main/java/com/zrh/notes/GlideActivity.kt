package com.zrh.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zrh.notes.databinding.ActivityGlideBinding

class GlideActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityGlideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityGlideBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnLoad.setOnClickListener {
            load()
        }

        mBinding.btnDelete.setOnClickListener {
            mBinding.imageContainer.removeAllViews()
            System.gc()
        }
    }

    private fun load() {
        Glide.get(this).clearMemory()
        mBinding.imageContainer.removeAllViews()
        val imageView = ImageView(this).apply {
            layoutParams =
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            mBinding.imageContainer.addView(this)
        }
        val url = "https://i4.3conline.com/images/piclib/200912/28/batch/1/50775/1261981340895qbsz9hwtxp.jpg"
        Glide.with(this).load(url).into(imageView)
        imageView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
            }

            override fun onViewDetachedFromWindow(v: View?) {
                Glide.with(this@GlideActivity).clear(imageView)
            }
        })
    }
}