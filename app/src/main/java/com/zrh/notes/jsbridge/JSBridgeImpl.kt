package com.zrh.notes.jsbridge

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast

/**
 *
 * @author zrh
 * @date 2023/8/27
 *
 */
class JSBridgeImpl(private val webView: WebView) : JSBridge {

    @JavascriptInterface
    override fun showDialog(title: String, msg: String) {
        Toast.makeText(webView.context, "title:$title msg:$msg", Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    override fun pickFile(callback: String) {
        val path = webView.context.cacheDir.absolutePath
        val jsCode = "window.JSSdk.onCallback('$callback','$path')"
        webView.post { webView.evaluateJavascript(jsCode, null) }
    }
}