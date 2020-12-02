package com.will.reader.print

import android.content.Context
import android.graphics.Color

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

    fun increaseTextSize(density: Float): PrintConfig{
        val increase = 2 * density // 2sp
        return this.copy(textSize = textSize+increase)
    }
    fun decreaseTextSize(density: Float): PrintConfig{
        val decrease = 2* density //2sp
        return this.copy(textSize = textSize-decrease)
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
            val textLineSpace = 10 * density
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