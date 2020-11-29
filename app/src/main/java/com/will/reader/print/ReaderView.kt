package com.will.reader.print

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

/**
 * created  by will on 2020/11/29 11:52
 */
class ReaderView(context: Context,private val printer: Printer,private val config: PrintConfig,private val paint: Paint): View(context) {


    override fun onDraw(canvas: Canvas?) {
        printer.fill().forEachIndexed{
            index,line ->
            canvas?.drawText(line,config.textMarginStart,(index * (config.textSize+config.textLineSpace)) + config.textMarginTop,paint)
        }
    }
}