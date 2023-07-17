package com.zrh.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.zrh.notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        addEntryButton("Glide", GlideActivity::class.java)
        addEntryButton("Okhttp", OkhttpActivity::class.java)
        addEntryButton("Annotation", this.javaClass)
    }

    private fun addEntryButton(name: String, clazz: Class<*>) {
        val button = AppCompatButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.WRAP_CONTENT)
            isAllCaps = false
            text = name
        }
        button.setOnClickListener { start(clazz) }
        mBinding.entryList.addView(button)
    }

    private fun start(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }
}