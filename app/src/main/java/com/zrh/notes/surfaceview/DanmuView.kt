package com.zrh.notes.surfaceview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.concurrent.ConcurrentLinkedQueue

/**
 *
 * @author zrh
 * @date 2023/9/15
 *
 * 使用SurfaceView简单实现弹幕效果
 */
class DanmuView(context: Context, attributes: AttributeSet?) : SurfaceView(context, attributes),
    SurfaceHolder.Callback2, Runnable {
    private var isActive = true

    private var mWidth = 0
    private var mHeight = 0
    private var mRowHeight = 0
    private val mItemSpacing = 30
    private val mStep = 5
    private var mRowCount = 3

    private val mQueue = ConcurrentLinkedQueue<Item>()
    private val mRowViewList = ArrayList<RowView>()

    init {
        holder.addCallback(this)
    }

    fun addItem(item: Item) {
        mQueue.offer(item)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Thread(this).start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mWidth = width
        mHeight = height
        mRowHeight = mHeight / mRowCount
        mRowViewList.clear()
        for (i in 0 until mRowCount) {
            mRowViewList.add(RowView())
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isActive = false
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
    }

    override fun run() {
        while (isActive) {
            addItemView()
            doAnimate()
            doDraw()
        }
    }

    private fun addItemView() {
        mRowViewList.forEachIndexed { index, rowView ->
            if (rowView.isAvailable(mWidth, mItemSpacing)) {
                val itemView = onCreateItemView(index)
                if (itemView != null) {
                    rowView.addItemView(itemView)
                }
            }
        }
    }

    private fun onCreateItemView(rowIndex: Int): ItemView? {
        if (!mQueue.isEmpty()) {
            val item = mQueue.poll()!!
            return ItemView(item.text, item.textColor, item.textSize).apply {
                x = (mWidth).toFloat()
                y = (rowIndex * mRowHeight).toFloat()
            }
        }
        return null
    }

    private fun doAnimate() {
        mRowViewList.forEach { it.animate(mStep) }
    }

    private fun doDraw() {
        var canvas: Canvas? = null
        try {
            canvas = holder.lockCanvas() ?: return

            canvas.drawColor(Color.WHITE)
            mRowViewList.forEach {
                it.draw(canvas)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            if (canvas != null) holder.unlockCanvasAndPost(canvas)
        }
    }
}

internal class RowView {

    private val mViewList = ArrayList<ItemView>()

    fun animate(step: Int) {
        if (mViewList.isEmpty()) return

        mViewList.forEach {
            it.x -= step
        }

        val firstView = mViewList[0]
        if (firstView.right < 0) {
            mViewList.removeAt(0)
        }
    }

    fun addItemView(itemView: ItemView) {
        mViewList.add(itemView)
    }

    fun isAvailable(maxWidth: Int, itemSpacing: Int): Boolean {
        if (mViewList.isEmpty()) return true
        val lastView = mViewList[mViewList.size - 1]
        return lastView.right + itemSpacing < maxWidth
    }

    fun draw(canvas: Canvas) {
        mViewList.forEach {
            it.draw(canvas)
        }
    }
}

internal class ItemView(private val content: String, color: Int, fontSize: Float) {
    var x = 0f
    var y = 0f
    var width = 0
    var height = 0

    val right: Float get() = x + width

    private val mPaint = Paint()
    private val bgPaint = Paint()
    private val padding = 20

    init {
        mPaint.color = color
        mPaint.flags = Paint.ANTI_ALIAS_FLAG
        mPaint.textSize = fontSize

        bgPaint.color = Color.parseColor("#7F000000")

        val rect = Rect()
        mPaint.getTextBounds(content, 0, content.length, rect)
        width = rect.width() + 2 * padding
        height = rect.height() + 2 * padding
    }

    fun draw(canvas: Canvas) {
        val radius = height / 2f
        canvas.drawRoundRect(x, y, x + width, y + height, radius, radius, bgPaint)
        canvas.drawText(content, x + padding, y + height - padding, mPaint)
    }
}

data class Item(
    val text: String,
    val textColor: Int,
    val textSize: Float
)