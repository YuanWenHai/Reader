package com.will.reader.chapterList.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.will.reader.util.LOG_TAG
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * created  by will on 2020/12/17 10:42
 */
class IndexRecyclerView(context: Context,attributeSet: AttributeSet): RelativeLayout(context,attributeSet){


    private val recyclerView: RecyclerView = RecyclerView(context)


    fun recycler(): RecyclerView{
        return recyclerView
    }

    init {
        val bubble = IndexBubble(context)
        val touchBar = IndexTouchBar(context).also {
            it.setCallback(object: IndexTouchBar.TouchBarEventCallback{
                override fun onMove(value: Int) {
                    bubble.visibility = View.VISIBLE
                    bubble.setContent("${value}%")
                    recyclerView.adapter?.let {
                        adapter ->
                        val pos = max(0,(adapter.itemCount * value / 100f).roundToInt() - 1)
                        recyclerView.scrollToPosition(pos)
                       /* val msg = "total count is ${adapter.itemCount} \n current position is $pos \n current value is ${value/100f}"
                        Log.e(LOG_TAG,msg)*/
                    }
                }

                override fun onUp(value: Int) {
                    bubble.visibility = View.INVISIBLE
                }

                override fun onCancel() {
                    bubble.visibility = View.INVISIBLE
                }
            })
        }
        val touchBarParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT).also {
            it.addRule(ALIGN_PARENT_END)
            it.marginEnd = 10 * resources.displayMetrics.density.toInt()
            it.topMargin = 100 * resources.displayMetrics.density.toInt()
            it.bottomMargin = 100 * resources.displayMetrics.density.toInt()
        }
        bubble.visibility = View.INVISIBLE
        val bubbleParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT).also {
            it.addRule(CENTER_IN_PARENT)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        val recyclerParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)

        addView(recyclerView,recyclerParams)
        addView(touchBar,touchBarParams)
        addView(bubble,bubbleParams)

    }

}