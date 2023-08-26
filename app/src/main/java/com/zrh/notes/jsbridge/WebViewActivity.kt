package com.zrh.notes.jsbridge

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.zrh.notes.databinding.ActivityWebBinding

/**
 *
 * @author zrh
 * @date 2023/8/27
 * 不建议直接在xml中定义WebView，推荐动态添加WebView防止内存泄漏，传入WebView的Context可以使用ApplicationContext，在Activity销毁时将WebView移除并置空。
 */
class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 配置WebView
        binding.webView.settings.apply {
            javaScriptEnabled = true
            // 设置屏幕自适应
            useWideViewPort = true
            loadWithOverviewMode = true
            // 设置缩放，下面两个方法需一起使用
            setSupportZoom(true)
            builtInZoomControls = true
            // 隐藏原生缩放控件
            displayZoomControls = false
            // 关闭缓存
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            // 设置可以访问文件
            allowFileAccess = true
            //支持通过JS打开新窗口
            javaScriptCanOpenWindowsAutomatically = true
            //支持自动加载图片
            loadsImagesAutomatically = true
            //设置编码格式
            defaultTextEncodingName = "utf-8"
        }

        // 设置WebViewClient
        binding.webView.webViewClient = object : WebViewClient() {
            // 拦截URL请求
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (request.url.scheme == "jsbridge") {

                    return true
                }

                return super.shouldOverrideUrlLoading(view, request)
            }

            // 加载页面资源时回调
            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            // 加载页面的服务器报错
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                Toast.makeText(this@WebViewActivity, "Error: $error", Toast.LENGTH_SHORT).show()
            }

            // SSL连接错误
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
                Toast.makeText(this@WebViewActivity, "SSLError: $error", Toast.LENGTH_SHORT).show()

            }
        }

        // 设置WebChromeClient
        binding.webView.webChromeClient = object : WebChromeClient() {
            // 监听页面初始化进度
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progress.progress = newProgress
                if (newProgress == 100) {
                    binding.progress.isVisible = false
                }
            }

            // 获取到网页标题
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                setTitle(title)
            }


        }

        // 设置JSBridge
        binding.webView.addJavascriptInterface(JSBridgeImpl(binding.webView), "JSBridge")

        // 加载本地页面
        binding.webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onBackPressed() {
        // 控制WebView页面返回
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        binding.webView.clearHistory()
        binding.root.removeView(binding.webView)
        // WebView调动destroy时Activity还保存在WebView中，所以需要先移除WebView在进行销毁
        binding.webView.destroy()
    }
}