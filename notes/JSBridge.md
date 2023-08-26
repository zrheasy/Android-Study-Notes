# JSBridge简介

JSBridge是以JavaScript引擎或WebView容器作为媒介，通过协定协议进行通信，实现Native端和Web端双向通信的一种机制。

在Hybrid开发模式下，H5页面通常需要调用原生的功能，例如选择文件、使用蓝牙、使用定位等，原生代码也需要向H5页面发送通知更新页面状态，这时就需要用到JSBridge提供的机制来进行通信。

### 通信原理

JavaScript调用原生方法时，JSBridge会将原生接口封装成JavaScript接口提供给JS端调用； 原生调用JavaScript方法时，JSBridge则将JavaScript接口封装成原生接口提供给原生端调用；

### 原生调用JS方法

在Android中可以通过WebView.evaluateJavascript(String jsCode, ValueCallback callback)方法执行JS代码。

```
String jsCode = String.format("window.showWebDialog('%s')", text);
webView.evaluateJavascript(jsCode, new ValueCallback<String>() {
  @Override
  public void onReceiveValue(String value) {

  }
});
```

### JS调用原生方法

JS调用原生方法有两种方式，一种是拦截URL请求，另一种是向WebView注入JS API。

在Android中可以重写WebViewClient的shouldOverrideUrlLoading进行URL的拦截，通常需要和Web端协商需要拦截的URL，此外URL长度会受到限制切记传递过多参数。

```kotlin
binding.webView.webViewClient = object : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (request.url.scheme == "jsbridge") {

            return true
        }
        return super.shouldOverrideUrlLoading(view, request)
    }
}
```

原生端可以向JS的window对象注入原生实现的JS接口，在JS端可直接通过全局的window调用注入的JS接口，在Android中可以通过WebView.addJavascriptInterface(Object bridge,
String name)注入JS接口。

```kotlin
// 实现JS接口
class JSBridgeImpl(private val webView: WebView) : JSBridge {

    @JavascriptInterface
    override fun showDialog(title: String, msg: String) {
        Toast.makeText(webView.context, "title:$title msg:$msg", Toast.LENGTH_SHORT).show()
    }
}
// 注入全局JS对象
binding.webView.addJavascriptInterface(JSBridgeImpl(binding.webView), "JSBridge")

// JS端调用原生接口
window.JSBridge.showDialog(title, msg);
```

### 实现带回调的JS调用

当JS端调用原生的异步方法时，原生需要等待任务完成后将数据返回给JS端，这时就需要设计一个JS回调提供给JS端接收数据。

一种简单的方案就是在JS端维护一个callback映射，在JS端调用原生异步方法时生成一个id与callback绑定，并将id传递给原生接口，原生接口接收callbackId然后在任务完成后调用JS的统一回调接口，传入callbackId和数据。

```
// JS端代码
<script>
  let id = 1;

  const callbackMap = {};

  window.JSSdk = {
  
    pickFile(callback) {
      const callbackId = id++;
      callbackMap[callbackId] = callback;
      window.JSBridge.pickFile(callbackId);
    },

    onCallback(callbackId, value) {
      if (callbackMap[callbackId]) {
        callbackMap[callbackId](value);
        delete callbackMap[callbackId];
      }
    }
  };

  document.querySelector('#pickFile').addEventListener('click', e => {
    window.JSSdk.pickFile(value => window.alert('选择文件：' + value));
  });

</script>

// 原生端代码
@JavascriptInterface
override fun pickFile(callback: String) {
    val path = webView.context.cacheDir.absolutePath
    val jsCode = "window.JSSdk.onCallback('$callback','$path')"
    webView.post { webView.evaluateJavascript(jsCode, null) }
}
```
