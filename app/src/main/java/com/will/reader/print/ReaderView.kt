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
        canvas?.let {
            printer.draw(it)
        }

    }
}