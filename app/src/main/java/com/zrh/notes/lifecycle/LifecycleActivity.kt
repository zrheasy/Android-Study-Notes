package com.zrh.notes.lifecycle

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.zrh.notes.databinding.ActivityLifecycleBinding

/**
 *
 * @author zrh
 * @date 2023/7/17
 *
 */
class LifecycleActivity : AppCompatActivity() {

    private val count = MutableLiveData(0)
    private lateinit var mBinding: ActivityLifecycleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLifecycleBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        count.observe(this) {
            mBinding.tvResult.text = "Result: $it"
            Log.d("Lifecycle", "receiver: $it")
        }

        mBinding.btnSet.setOnClickListener {
            var newCount = count.value!! + 1
            Log.d("Lifecycle", "set: $newCount")
            count.value = newCount

            newCount++
            Log.d("Lifecycle", "set: $newCount")
            count.value = newCount
        }

        mBinding.btnPost.setOnClickListener {
            var newCount = count.value!! + 1
            Log.d("Lifecycle", "post: $newCount")
            count.postValue(newCount)

            newCount++
            Log.d("Lifecycle", "post: $newCount")
            count.postValue(newCount)
        }
    }

}