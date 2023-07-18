package com.zrh.notes.lifecycle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *
 * @author zrh
 * @date 2023/7/18
 *
 */
class CountViewModel : ViewModel() {
    val count = MutableLiveData(0)

    fun increase() {
        count.value = count.value!! + 1
    }
}