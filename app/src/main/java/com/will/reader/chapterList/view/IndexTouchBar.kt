package com.will.reader.chapterList.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.will.reader.R
import com.will.reader.util.LOG_TAG
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.truncate

/**
 * created  by will on 2020/12/17 10:43
 */
class IndexTouchBar(context: Context): View(context) {

    private val barWidth: Float = 18 * context.resources.displayMetrics.density
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var callback: TouchBarEventCallback? = null

    init {
        mPaint.color = context.resources.getColor(R.color.colorPrimaryTranslucent)
    }

    fun setCallback(callback: TouchBarEventCallback){
        this.callback = callback
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.let { drawBar(canvas) }
    }

    private var touched = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {touched = true}
            MotionEvent.ACTION_MOVE ->{handleTouchMove(event)}
            MotionEvent.ACTION_UP -> {handleTouchUp(event)}
            MotionEvent.ACTION_CANCEL -> {handleTouchCancel(event)}
            //MotionEvent.ACTION_OUTSIDE -> {touched = false}
            else  -> super.onTouchEvent(event)

        }
         return true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(barWidth.toInt(),mHeight)
        moveThresholds = mHeight/100.toFloat()
    }

    private fun drawBar(canvas: Canvas){
        canvas.drawRect(0f,0f,barWidth,height.toFloat(),mPaint)
    }


    private var moveThresholds = 10f
    private var lastY = 0f
    private fun handleTouchMove(event: MotionEvent){
        callback?.let {
            if (touched && abs(event.y - lastY) > moveThresholds){
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(15)
                val value = ((event.y / height.toFloat())*100).roundToInt()/100f
                it.onMove(value)
                lastY = event.y
            }
        }
    }
    private fun handleTouchUp(event: MotionEvent){
        callback?.let {
            if(touched){
                val value = lastY / height.toFloat()
                it.onUp(value)
            }
        }
        touched = false
    }
    private fun handleTouchCancel(event: MotionEvent){
        touched = false
        callback?.onCancel()
    }

    interface TouchBarEventCallback{
        fun onMove(value: Float)
        fun onUp(value: Float)
        fun onCancel()
    }

}