# 理解MeasureSpec

在View.onMeasure(int widthMeasureSpec, int heightMeasureSpec)
回调方法中会接收到widthMeasureSpec和heightMeasureSpec，这里的MeasureSpec是个Int型，但它其实由两部分构成：Mode和Size。

MeasureSpec的高2位为Mode，低30位为Size，可以使用如下方法解析出来：

```groovy
int specMode = MeasureSpec.getMode(spec);
int specSize = MeasureSpec.getSize(spec);
```

MeasureSpec代表的意思是父布局结合自己的尺寸和子View想要的尺寸给出的一个特定模式下的尺寸，然后交给子View来判断自己需要多大的尺寸。

## MeasureMode

要想知道如何计算尺寸就需要先搞清楚Mode代表什么意思，Mode定义了3种测量模式：

1. UNSPECIFIED：代表父布局不知道子View需要多大的尺寸，需要子View自己决定，此时Size=0。
2. AT_MOST：代表父布局不知道子View需要多大的尺寸，需要子View自己决定，但是需要的尺寸不能超出给定的Size，此时Size>0。
3. EXACTLY：代表父布局已经明确了子View需要的尺寸，子View可以直接使用给定的Size，此时Size>0。

## 父布局如何给子View计算的尺寸

可以看一下ViewGroup中提供的计算方法：

```groovy
// 这里的spec是当前布局的父布局给定的MeasureSpec
public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
    int specMode = MeasureSpec.getMode(spec);
    int specSize = MeasureSpec.getSize(spec);
    // 减掉padding后才是留给子View的参考尺寸
    int size = Math.max(0, specSize - padding);

    int resultSize = 0;
    int resultMode = 0;

    switch (specMode) {
    // 父布局给了一个明确的尺寸，那么当前ViewGroup有了确定的尺寸
        case MeasureSpec.EXACTLY:
            // 子View申请了明确的尺寸，那么就满足它
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // 子View想要当前ViewGroup的尺寸，那么也满足它
                resultSize = size;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // 子View不能确定自己的尺寸，那么给它最大的尺寸不能超过此尺寸
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

            // 父布局给了一个最大的尺寸，当前ViewGroup可用的尺寸不能超过最大尺寸
        case MeasureSpec.AT_MOST:
            if (childDimension >= 0) {
                // 子View申请了明确的尺寸，那么就满足它
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // 子View想要当前ViewGroup的尺寸，那么给它最大的尺寸不能超过此尺寸
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // 子View不能确定自己的尺寸，那么给它最大的尺寸不能超过此尺寸
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

            // 父布局未给出任何尺寸，需要自己确定
        case MeasureSpec.UNSPECIFIED:
            if (childDimension >= 0) {
                // 子View申请了明确的尺寸，那么就满足它
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // 子View想要当前ViewGroup的尺寸，但当前布局并没有明确的尺寸，所以让子View自己确定
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // 子View不能确定自己的尺寸，当前布局也没有明确的尺寸，所以让子View自己确定
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            }
            break;
    }
    return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
}
```

## 子View如何计算自己需要的尺寸

可以看一下View中的计算方法：

```groovy
// size为需要的最小尺寸，childMeasuredState为测量后尺寸的状态
public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
    final int specMode = MeasureSpec.getMode(measureSpec);
    final int specSize = MeasureSpec.getSize(measureSpec);
    final int result;
    switch (specMode) {
    // 父布局给了一个最大的尺寸，不能超过该尺寸
        case MeasureSpec.AT_MOST:
            if (specSize < size) {
                result = specSize | MEASURED_STATE_TOO_SMALL;
            } else {
                result = size;
            }
            break;
            // 父布局给了明确的尺寸，那么直接使用
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
            // 父布局未给出明确尺寸，那么就使用自己的最小尺寸
        case MeasureSpec.UNSPECIFIED:
        default:
            result = size;
    }
    return result | (childMeasuredState & MEASURED_STATE_MASK);
}
```
