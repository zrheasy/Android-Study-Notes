package com.zrh.notes.anr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 *
 * @author zrh
 * @date 2023/7/19
 *
 */
class ANRBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Thread.sleep(20_000)
        Log.d("TestANR", "ANRBroadcastReceiver finished")
    }
}