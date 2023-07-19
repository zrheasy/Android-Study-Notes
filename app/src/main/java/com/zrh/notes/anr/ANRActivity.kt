package com.zrh.notes.anr

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zrh.notes.databinding.ActivityAnrBinding

/**
 *
 * @author zrh
 * @date 2023/7/19
 *
 */
class ANRActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAnrBinding
    private val mReceiver by lazy {
        ANRBroadcastReceiver()
    }
    private val filter by lazy {
        IntentFilter("TestANR")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAnrBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnBlock.setOnClickListener {
            Thread.sleep(10_000)
        }

        mBinding.btnInput.setOnClickListener {
            Log.d("TestANR", "InputEvent finished")
        }

        mBinding.btnService.setOnClickListener {
            startService(Intent(this, ANRService::class.java))
        }

        mBinding.btnBroadcast.setOnClickListener {
            sendOrderedBroadcast(Intent("TestANR"), null)
        }

        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }
}