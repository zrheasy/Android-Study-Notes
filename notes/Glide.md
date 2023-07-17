# Glide加载流程

1. 初始化Glide（单例）和RequestManager（管理生命周期范围内的Request和Target）。
2. 创建RequestBuilder，为Request创建做准备。
3. 创建Request（发起请求的实例）和 Target（接收数据的对象，与一个Request绑定） ，启动Request。
4. Request通过Engine（管理着EngineJob，防止重复加载，首先从活动缓存和内存缓存中获取）加载数据。
5. Engine通过复用和新建EngineJob（加载任务的实例，通过DecodeJob来加载数据）来执行加载任务。
6. DecodeJob（负责加载数据、转码和解码，内部会分阶段使用DataFetcherGenerator加载数据，然后在对数据进行decode，再根据资源类型决定是否进行transform和缓存，最后在进行transcode） 
7. ResourceCacheGenerator（加载解码和转码后的缓存数据）
8. DataCacheGenerator（加载原始的缓存数据）
9. SourceGenerator（加载原始远程数据，然后缓存到本地，再通过DataCacheGenerator返回对应的数据） 
10. 匹配出合适的ModelLoader来创建DataFetcher，从指定的Model中加载数据
11. 将DataFetcher获取的数据交给DecodeJob进行decode和transcode
12. 最后DecodeJob将transcode后的数据一层层提交到Target

**Glide何时进行资源的回收？**

1. 在回调onLowMemory时释放内存缓存资源。
2. 当activity、fragment退出时会将活动缓存放入到内存缓存。

**如何在View退出时回收活动资源？**

1. 调用View.addOnAttachStateChangeListener添加退出监听。
2. 在onViewDetachedFromWindow时调用Glide.with(this).clear(imageView)回收活动资源。