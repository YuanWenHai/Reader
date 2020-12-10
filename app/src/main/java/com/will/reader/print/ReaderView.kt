package com.will.reader.print

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.will.reader.util.LOG_TAG

/**
 * created  by will on 2020/11/29 11:52
 */
class ReaderView(context: Context,attributeSet: AttributeSet): View(context,attributeSet) {

    private var clickFlag = false
    private var onClick: ((which: Int) -> Unit)? = null
    private var printConfig: PrintConfig? = null
    private var printerPage: Printer.PrinterPage? = null
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> clickFlag = true
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

    fun setConfig(config: PrintConfig){
        this.printConfig = config
        this.mPaint.textSize = config.textSize
    }
    fun submitPage(page: Printer.PrinterPage){
        if(printConfig == null){
            Log.e(LOG_TAG,"must invoke setConfig before")
            return
        }
        this.printerPage = page
        invalidate()
    }


    fun setOnClickListener(listener: (which: Int) -> Unit){
        this.onClick = listener
    }

    override fun onDraw(canvas: Canvas?) {
        if(canvas != null && printConfig != null && printerPage != null){
            val config: PrintConfig = printConfig!!
            val page: Printer.PrinterPage = printerPage!!
            canvas.drawColor(config.backgroundColor)
            page.lines.forEachIndexed{
                    index, line ->
                val x = config.textMarginStart
                val y = config.textMarginTop + ((index+1)*(config.textSize+config.textLineSpace))
                canvas.drawText(line,x,y,mPaint)
            }
            drawBottomBar(canvas,config,page)
        }
    }

    private fun drawBottomBar(canvas: Canvas,config: PrintConfig,page: Printer.PrinterPage){
        val bottomBarTextSize = config.bottomBarHeight * 0.66f
        val y = height - (config.bottomBarHeight * 0.34f)
        val timeText = page.timeText
        val batteryLevelText = page.batteryText
        val progressText = page.progressText
        mPaint.textSize = bottomBarTextSize
        //draw battery level text
        canvas.drawText(batteryLevelText,config.textMarginStart,y,mPaint)
        //draw progress text
        val progressX = (width - mPaint.measureText(progressText)) * 0.5f
        canvas.drawText(progressText,progressX,y,mPaint)
        //draw time text
        val timeX = width - config.textMarginEnd - mPaint.measureText(timeText)
        canvas.drawText(timeText,timeX,y,mPaint)
        mPaint.textSize = config.textSize

    }

    private fun handleActonUp(event: MotionEvent) {
        val viewWidth = width
        when (event.x) {
            in 0f..(viewWidth / 3f) -> onClick?.let { it(LEFT_CLICK) }
            in ((width / 3f) * 2)..width.toFloat() -> onClick?.let { it(RIGHT_CLICK) }
            else -> onClick?.let { it(CENTER_CLICK) }
        }
    }
    companion object {
        const val LEFT_CLICK = 0
        const val CENTER_CLICK = 1
        const val RIGHT_CLICK = 2
    }
}