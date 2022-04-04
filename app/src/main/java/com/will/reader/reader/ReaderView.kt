package com.will.reader.reader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.will.reader.util.LOG_TAG
import kotlin.math.abs

/**
 * created  by will on 2020/11/29 11:52
 */
class ReaderView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var clickFlag = false
    private var downX = 0f
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop * 5
    private var onClick: ((which: Int) -> Unit)? = null
    private var printConfig: PrintConfig? = null
    private var mContent = Content(emptyList(), "", "", "")
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBackground = BitmapFactory.decodeStream(context.assets.open("bg.png"))
    private val mBitmapRect by lazy {
        Rect(0, 0, width, height)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                clickFlag = true
                downX = event.x
            }
            MotionEvent.ACTION_CANCEL -> clickFlag = false
            MotionEvent.ACTION_OUTSIDE -> clickFlag = false
            MotionEvent.ACTION_UP -> {
                if (clickFlag) {
                    handleActonUp(event)
                    clickFlag = false
                }
            }

        }
        return true
    }

    fun setConfig(config: PrintConfig) {
        this.printConfig = config
        this.mPaint.textSize = config.textSize
    }

    fun submitContent(content: Content) {
        if (printConfig == null) {
            Log.e(LOG_TAG, "must invoke setConfig before")
            return
        }
        mContent = content
        invalidate()
    }


    fun setOnClickListener(listener: (which: Int) -> Unit) {
        this.onClick = listener
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null && printConfig != null) {
            val config: PrintConfig = printConfig!!
            canvas.drawBitmap(mBackground, null, mBitmapRect, mPaint)
            mContent.lines.forEachIndexed { index, line ->
                val x = config.textMarginStart
                val y =
                    config.textMarginTop + ((index + 1) * (config.textSize + config.textLineSpace))
                canvas.drawText(line, x, y, mPaint)
            }
            drawBottomBar(canvas, config, mContent)
        }
    }

    private fun drawBottomBar(canvas: Canvas, config: PrintConfig, content: Content) {
        val bottomBarTextSize = config.bottomBarHeight * 0.66f
        val y = height - (config.bottomBarHeight * 0.34f)
        val timeText = content.time
        val batteryLevelText = content.batteryLevel
        val progressText = content.progress
        mPaint.textSize = bottomBarTextSize
        //draw battery level text
        canvas.drawText(batteryLevelText, config.textMarginStart, y, mPaint)
        //draw progress text
        val progressX = (width - mPaint.measureText(progressText)) * 0.5f
        canvas.drawText(progressText, progressX, y, mPaint)
        //draw time text
        val timeX = width - config.textMarginEnd - mPaint.measureText(timeText)
        canvas.drawText(timeText, timeX, y, mPaint)
        mPaint.textSize = config.textSize

    }


    private fun handleActonUp(event: MotionEvent) {
        val viewWidth = width
        val offset = downX - event.x
        if (abs(offset) > mTouchSlop) {
            if (offset > 0) {
                onClick?.let { it(RIGHT_CLICK) }
            } else {
                onClick?.let { it(LEFT_CLICK) }
            }
            return
        }

        when (event.x) {
            in 0f..(viewWidth / 3f) -> onClick?.let { it(LEFT_CLICK) }
            in ((width / 3f) * 2)..width.toFloat() -> onClick?.let { it(RIGHT_CLICK) }
            else -> onClick?.let { it(CENTER_CLICK) }
        }
    }

    data class Content(
        val lines: List<String>,
        val time: String,
        val progress: String,
        val batteryLevel: String
    )

    companion object {
        const val LEFT_CLICK = 0
        const val CENTER_CLICK = 1
        const val RIGHT_CLICK = 2
    }
}