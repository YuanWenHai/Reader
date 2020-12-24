package com.will.reader.reader

import android.content.Context
import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

/**
 * created  by will on 2020/11/29 12:00
 */
data class PrintConfig(
        val textSize: Float,
        val textMarginStart: Float,
        val textMarginEnd: Float,
        val textMarginTop: Float,
        val textMarginBottom: Float,
        val textLineSpace: Float,
        val bottomBarHeight: Float,
        val textColor: Int,
        val backgroundColor: Int,
)
{

    /**
     * 字体增加2sp，最大限制为40sp
     */
    fun increaseTextSize(density: Float): PrintConfig{
        val increase = 2 * density // 2sp
        val newValue = min(textSize + increase,40 * density) // maximum size is 40sp
        val lineSpace = newValue/3
        return this.copy(textSize = newValue,textLineSpace = lineSpace)
    }

    /**
     * 字体缩小2sp，最小限制8sp
     */
    fun decreaseTextSize(density: Float): PrintConfig{
        val decrease = 2* density //2sp
        val newValue = max(textSize - decrease,8 * density)// minimum size is 8 sp
        val lineSpace = newValue/3
        return this.copy(textSize = newValue,textLineSpace = lineSpace)
    }

    companion object{
        fun default(context: Context): PrintConfig{
            //to dp
            val density = context.resources.displayMetrics.density
           return default(density)
        }
        fun default(density: Float): PrintConfig{
            val textSize = 14 * density
            val marginStart = 12 * density
            val marginEnd = 12 * density
            val marginTop = 12 * density
            val marginBottom = 12 * density
            val textLineSpace = textSize/3
            val bottomBarHeight = 24 * density
            val textColor = Color.BLACK
            val backgroundColor = Color.WHITE
            return PrintConfig(
                textSize = textSize,
                textMarginStart = marginStart,
                textMarginEnd = marginEnd,
                textMarginTop = marginTop,
                textMarginBottom = marginBottom,
                textLineSpace, bottomBarHeight, textColor, backgroundColor
            )
        }
    }

}