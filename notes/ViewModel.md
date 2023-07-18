# Android ViewModel实现原理
Android ViewModel是Jetpack开发套件中一个支持实现MVVM架构的基础组件，它针对Android页面特性提供了状态保存的机制。

## 组成部件
要实现ViewModel的状态保存需要几个部件的支持，下面列出相关组成部件：
- ViewModel：ViewModel基类，继承自ViewModel的子类才可以被保存
- ViewModelProvider：ViewModel的提供者，可以获取一个被保存的ViewModel或新建一个ViewModel
- Factory：用于创建新的ViewModel，实现类为SavedStateViewModelFactory
- ViewModelStore：用于存储ViewModel
- ViewModelStoreOwner：持有一个ViewModelStore，实现类为Fragment和ComponentActivity

## 使用方式
```kotlin
class ViewModelActivity : AppCompatActivity() {
    private lateinit var mViewModel: CountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. 创建一个ViewModelProvider
        val provider = ViewModelProvider(this)
        // 2. 通过ViewModelProvider获取ViewModel
        mViewModel = provider.get(CountViewModel::class.java)
    }
}
// 使用ViewModel的扩展库还可以一行代码实现
private val mViewModel: CountViewModel by viewModels()
```

## 实现原理
我们知道Activity在App配置发生变化时会进行重建（如切换屏幕方向、切换系统语言等），如果页面的数据没有得到保存那么页面新建后所有的状态都是初始状态，这会打断用户当前操作造成不好的体验，而ViewModel正好实现了状态保存的机制可以在页面重建后恢复状态。

当需要获取一个指定类型的ViewModel时，会通过ViewModelProvider.get(Class<T> modelClass)方法获取，此时会根据Class的名字获取对应的Key，再通过ViewModelStore查找，如存在此ViewModel的实例则返回，不存在则通过Factory新建一个ViewModel并存入ViewModelStore中，然后返回。

因为Fragment和ComponentActivity实现了ViewModelStoreOwner接口，因此可以获取到ViewModelStore，当页面发生重建时由于ViewModelStore内的缓存并不会被清除，所以我们可以拿到之保存的ViewModel而不用新建，这样ViewModel内所有状态数据都是最新的。

