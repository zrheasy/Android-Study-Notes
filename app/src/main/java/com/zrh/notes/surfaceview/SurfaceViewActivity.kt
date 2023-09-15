package com.zrh.notes.surfaceview

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zrh.notes.databinding.ActivitySurfaceviewBinding

/**
 *
 * @author zrh
 * @date 2023/9/15
 *
 */
class SurfaceViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurfaceviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySurfaceviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        autoText()

        binding.btnSend.setOnClickListener {
            send()
        }
    }

    private fun autoText(){
        val delay = Math.random() * 1000 * 3
        val task = Runnable {
            binding.danmuview.addItem(getItem("Hello World!"))
            autoText()
        }
        binding.root.postDelayed(task, delay.toLong())
    }

    private fun send() {
        val text = binding.editText.text.toString()
        if (text.isBlank()) return

        binding.danmuview.addItem(getItem(text))

        binding.editText.setText("")
    }

    private fun getItem(text: String): Item {
        val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA)
        val textColor = colors[((Math.random() * 100) % 4).toInt()]
        val textSize = resources.displayMetrics.scaledDensity * 24
        return Item(text, textColor, textSize)
    }
}