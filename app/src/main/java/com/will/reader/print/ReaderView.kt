package com.will.reader.print

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * created  by will on 2020/11/29 11:52
 */
class ReaderView(context: Context,attributeSet: AttributeSet): View(context,attributeSet) {

    private var clickFlag = false
    private var printer: Printer? = null


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

    fun setPrinter(printer: Printer){
        this.printer = printer
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            printer?.draw(it)
        }
    }
    fun refresh(){
        invalidate()
    }

    private fun performLeftClick(){
        printer?.pageUp()
        invalidate()
    }
    private fun performRightClick(){
        printer?.pageDown()
        invalidate()
    }

    private fun handleActonUp(event: MotionEvent){
        val viewWidth = width
        if(event.x < (viewWidth/2)){
            performLeftClick()
        }else{
            performRightClick()
        }
    }
}