package com.will.reader.chapterList.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.will.reader.R
import kotlin.math.min

/**
 * created  by will on 2020/12/17 11:26
 */
class IndexBubble(context: Context): View(context) {

    private var mContent = ""
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var contentX = 0f
    private var contentY = 0f

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {drawBubble(it)}
    }

    private fun drawBubble(canvas: Canvas){
        mPaint.color = resources.getColor(R.color.colorPrimaryTranslucent)
        canvas.drawCircle(centerX,centerY,radius,mPaint)

        mPaint.color = resources.getColor(R.color.white)
        canvas.drawText(mContent,contentX,contentY,mPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpec = if(heightMode == MeasureSpec.AT_MOST) MeasureSpec.getSize(heightMeasureSpec)/3 else MeasureSpec.getSize(heightMeasureSpec)
        val widthSpec = if(widthMode == MeasureSpec.AT_MOST) MeasureSpec.getSize(widthMeasureSpec)/3 else MeasureSpec.getSize(widthMeasureSpec)
        val dimen = min(widthSpec,heightSpec)
        setMeasuredDimension(dimen,dimen)
        centerX = dimen/2.toFloat()
        centerY = centerX
        radius = dimen/2.toFloat()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }


    fun setContent(content: String){
        mContent = content
        contentX = (width + mPaint.measureText(mContent))/2f
        contentY = height/2f
        invalidate()
    }
}