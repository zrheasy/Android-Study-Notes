package com.zrh.notes.anr

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 *
 * @author zrh
 * @date 2023/7/19
 *
 */
class ANRService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread.sleep(20_000)
        Log.d("TestANR", "ANRService finished")
        return super.onStartCommand(intent, flags, startId)
    }
}