package com.zrh.notes.jsbridge

/**
 *
 * @author zrh
 * @date 2023/8/27
 *
 */
interface JSBridge {
    fun showDialog(title: String, msg: String)
    fun pickFile(callback: String)
}