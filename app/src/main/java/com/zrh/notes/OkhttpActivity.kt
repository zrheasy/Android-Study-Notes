package com.zrh.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.zrh.notes.databinding.ActivityOkhttpBinding
import okhttp3.*
import java.io.IOException

class OkhttpActivity : AppCompatActivity() {

    private lateinit var client: OkHttpClient
    private lateinit var mBinding: ActivityOkhttpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOkhttpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        client = OkHttpClient.Builder().build()
        mBinding.btnLoad.setOnClickListener { load() }
    }

    private fun load() {
        val request = Request.Builder()
            .url("https://www.baidu.com")
            .get()
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { toast("Error $e") }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val data = response.body?.string() ?: "null"
                    runOnUiThread { onResult(data) }
                } else {
                    runOnUiThread { toast("Error ${response.code}") }
                }
            }
        })
    }

    private fun onResult(data: String) {
        mBinding.tvResult.text = "请求结果：$data"
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}