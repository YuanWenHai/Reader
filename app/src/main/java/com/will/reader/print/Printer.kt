package com.will.reader.print

import android.graphics.Canvas
import android.graphics.Paint
import android.util.DisplayMetrics
import com.will.reader.data.model.Book
import java.io.File
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

/**
 * created  by will on 2020/11/29 11:43
 */
class Printer(private val book: Book,private val config: PrintConfig,private val screen: DisplayMetrics,private val paint: Paint){
    private val bookFile = File(book.path)
    private val bookBytes: MappedByteBuffer
    private val byteChannel: FileChannel
    private var pageBegin = book.readProgressInByte
    private var currentParagraphStart = 0
    init {
        val file = File(book.path)
        byteChannel = RandomAccessFile(file,"r").channel
        bookBytes = byteChannel.map(FileChannel.MapMode.READ_ONLY,0,file.length())
    }

    /**
     * 将一段byte读为string
     * @param start 起始位置,inclusive
     * @param end 终止位置,exclusive
     */
    private fun readParagraphString(start: Int,end: Int): String{
        val bytes = ByteArray(end-start)
        for (i in bytes.indices){
            bytes[i] = bookBytes.get(start+i)
        }
        return String(bytes,Charset.forName(book.encode))
    }
    /**
     *寻找下一段末尾(/n)的byte位置,若已到文件末尾则返回文件长度
     * @param currentStart inclusive
     */
    private fun findNextParagraphStartPos(currentStart: Int): Int{
        val lf = 10.toByte()
        val cr = 13.toByte()
        if(currentStart == bookBytes.limit()){
            //reach the end
            return -1
        }
        for(i in currentStart until bookBytes.limit()){
            if(bookBytes[i] == lf){
                //i为段尾/n，i+1指向下一段首byte
                return i+1
            }
        }
        return bookBytes.limit()
    }

    private fun findLastParagraphStartPos(currentStart: Int): Int{
        val lf = 10.toByte() // \n
        if(currentStart == 0){
            return -1
        }
        // TODO: 2020/12/2  
        for(i in currentStart downTo 0){
            if(i != currentStart-1 && bookBytes[i] == lf){
                //当前i为段尾，+1为下一段首byte
                return i+1
            }
        }
        return 0
    }

    private fun measureLineCount(config: PrintConfig,screen: DisplayMetrics): Int{
        val availableHeight = screen.heightPixels - config.textMarginTop - config.textMarginBottom - config.bottomBarHeight
        val lineHeight = config.textSize + config.textLineSpace
        return (availableHeight/lineHeight).toInt()
    }

    /**
     * 正序测量该行应使用text多少字，returned index is exclusive
     */
    private fun measureLineIndexForward(text: String, config: PrintConfig, screen: DisplayMetrics): Int{
        val availableWidth = screen.widthPixels - config.textMarginStart - config.textMarginEnd
        paint.textSize = config.textSize
        for( i in 1 until text.length){
            if(paint.measureText(text.substring(0,i)) > availableWidth){
                return i
            }
        }
        return text.length
    }

    /**
     * 倒序测量改行应使用text字数
     */
    private fun measureLineIndexBackward(text: String,config: PrintConfig,screen: DisplayMetrics): Int{
        val availableWidth = screen.widthPixels - config.textMarginStart - config.textMarginEnd
        paint.textSize = config.textSize
        for(i in text.length-1 downTo  0){
            if(paint.measureText(text.substring(i,text.length)) > availableWidth){
                return i
            }
        }
        return text.length
    }

    fun pageDown(): List<String>{
        return compose()
    }
    fun pageUp(): List<String>{
        val lineCount = measureLineCount(config,screen)
        for (i in 0 until lineCount){
            val lastParagraphEndPos = findLastParagraphStartPos(currentParagraphStart)
            val paragraphStr = readParagraphString(lastParagraphEndPos+1,currentParagraphStart+1)
        }
    }


    private fun compose(): List<String>{
        val lineCount = measureLineCount(config,screen)
        val lines = mutableListOf<String>()
        var paragraph = ""
        pageBegin = currentParagraphStart
        for(i in 0 until lineCount){
            if(paragraph.isEmpty()){
                val newStart = findNextParagraphStartPos(currentParagraphStart)
                if(newStart == -1){
                    return lines
                }
                paragraph = readParagraphString(currentParagraphStart,newStart)
                currentParagraphStart = newStart
            }
            val lineEnd = measureLineIndexForward(paragraph,config,screen)
            lines.add(paragraph.substring(0,lineEnd))
            paragraph = paragraph.substring(lineEnd,paragraph.length)
        }
        val offsetCorrection = paragraph.toByteArray(Charset.forName(book.encode)).size
        currentParagraphStart -= offsetCorrection
        return lines
    }

    fun draw(canvas: Canvas){
        compose().forEachIndexed{
            index, line ->
            val x = config.textMarginStart
            val y = config.textMarginTop + ((index+1)*(config.textSize+config.textLineSpace))
            canvas.drawText(line,x,y,paint)
        }
    }
}