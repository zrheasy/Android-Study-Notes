package com.zrh.notes.annotation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zrh.notes.databinding.ActivityAnnotationBinding

/**
 *
 * @author zrh
 * @date 2023/7/17
 *
 */
@Model(name = "Test")
class AnnotationActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAnnotationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAnnotationBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.tvResult.text = "Annotations: ${getAnnotationInfo()}"
    }

    private fun getAnnotationInfo(): String {
        return ModelHandler.getAnnotations(this).joinToString { it }
    }
}