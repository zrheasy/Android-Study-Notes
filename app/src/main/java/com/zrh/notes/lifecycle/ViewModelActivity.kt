package com.zrh.notes.lifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zrh.notes.databinding.ActivityViewmodelBinding

/**
 *
 * @author zrh
 * @date 2023/7/18
 *
 */
class ViewModelActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityViewmodelBinding
    private lateinit var mViewModel: CountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityViewmodelBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val provider = ViewModelProvider(this)
        mViewModel = provider.get(CountViewModel::class.java)

        mViewModel.count.observe(this) {
            mBinding.tvResult.text = "Result: $it"
        }

        mBinding.btnAdd.setOnClickListener {
            mViewModel.increase()
        }
    }
}