package com.will.reader.chapterList.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * created  by will on 2020/12/17 10:42
 */
class IndexRecyclerView(context: Context): RelativeLayout(context) {


    constructor(context: Context,attributeSet: AttributeSet): this(context) {
    }

    init {
        layoutParams = ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val recyclerView = RecyclerView(context)
        addView(recyclerView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)

        val touchBarParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT).also {
            it.addRule(ALIGN_PARENT_END)
            // TODO: 2020/12/17  right margin not working?
            it.rightMargin = 10 * resources.displayMetrics.density.toInt()
            it.topMargin = 20 * resources.displayMetrics.density.toInt()
            it.bottomMargin = 20 * resources.displayMetrics.density.toInt()
        }
        val touchBar = IndexTouchBar(context).also {
            it.setCallback(object: IndexTouchBar.TouchBarEventCallback{
                override fun onMove(value: Float) {

                }

                override fun onUp(value: Float) {
                }

                override fun onCancel() {

                }
            })
        }

        addView(touchBar,touchBarParams)

        val bubbleParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT).also {
            it.addRule(CENTER_IN_PARENT)
        }
        addView(IndexBubble(context),bubbleParams)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}