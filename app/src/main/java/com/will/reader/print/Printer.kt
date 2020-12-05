package com.will.reader.print

import android.graphics.Canvas
import android.graphics.Paint
import android.util.DisplayMetrics
import com.will.reader.data.model.Book
import java.io.File
import java.io.RandomAccessFile
import java.lang.StringBuilder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

/**
 * created  by will on 2020/11/29 11:43
 */
class Printer(private var book: Book,private var config: PrintConfig,private val screen: DisplayMetrics){
    private val bookFile = File(book.path)
    private val bookBytes: MappedByteBuffer
    private val byteChannel: FileChannel
    private var pageBegin = book.readProgressInByte
    private var currentPosition = 0
    private var lines: List<String> = mutableListOf()
    private var paint: Paint
    init {
        val file = File(book.path)
        byteChannel = RandomAccessFile(file,"r").channel
        bookBytes = byteChannel.map(FileChannel.MapMode.READ_ONLY,0,file.length())
        paint = generatePaint(config)
    }

    /**
     * 修改config参数
     */
    fun setConfig(config: PrintConfig){
        this.config = config
        this.paint = generatePaint(config)
        prepare()
    }
    fun getConfig(): PrintConfig{
        return config
    }

    fun getCurrentBookStateForSave(): Book{
        val readProgressInByte = pageBegin
        val builder = StringBuilder()
        lines.forEach{builder.append(it)}
        val lastReadParagraph = builder.toString()
        val lastReadTime = System.currentTimeMillis()
        return book.copy(readProgressInByte = readProgressInByte,lastReadParagraph = lastReadParagraph,lastReadTime = lastReadTime)
    }
    fun setEncoding(encode: String){
        this.book = book.copy(encode = encode)
    }

    fun prepare(){
        print()
    }

    /**
     * 从当前page begin位置开始排版一页内容，并将current position 移动到下一页首个byte
     */
    fun print(){
        val page = compose(pageBegin)
        currentPosition = page.currentPosition
        lines = page.lines
    }

    /**
     * 从下一页首个byte位置开始排版一页内容，并将current position移动到排版后的下一页首个byte
     */
    fun pageDown(){
        pageBegin = currentPosition
        val page = compose(currentPosition)
        currentPosition = page.currentPosition
        lines = page.lines
    }

    /**
     *从当前page begin位置上翻一页排版一页内容，并将page begin移动到当前页首个byte，current position为下一页首个byte
     */
    fun pageUp(){
        val start = findLastPageBegin(pageBegin)
        val page = compose(start)
        pageBegin = start
        currentPosition = page.currentPosition
        lines = page.lines
    }

    fun draw(canvas: Canvas){
        lines.forEachIndexed{
                index, line ->
            val x = config.textMarginStart
            val y = config.textMarginTop + ((index+1)*(config.textSize+config.textLineSpace))
            canvas.drawText(line,x,y,paint)
        }
    }





    /**
     * 将一段byte读为string
     * @param start 起始位置,inclusive
     * @param end 终止位置,exclusive
     */
    private fun readParagraphString(start: Int,end: Int): String{
        val length = end - start
        if(length <= 0){
            return ""
        }
        val bytes = ByteArray(length)
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
        if(currentStart == 0){
            return 0
        }
        val lf = 10.toByte() // \n
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
    private fun measureLineIndexForward(text: String,  screen: DisplayMetrics): Int{
        val availableWidth = screen.widthPixels - config.textMarginStart - config.textMarginEnd
        for( i in 1 until text.length){
            if(paint.measureText(text.substring(0,i)) > availableWidth){
                return i - 1
            }
        }
        return text.length
    }

    /**
     * 倒序测量改行应使用text字数
     */
    private fun measureLineIndexBackward(text: String,config: PrintConfig,screen: DisplayMetrics): Int{
        val availableWidth = screen.widthPixels - config.textMarginStart - config.textMarginEnd
        var nextLineStart = 0
        var temp: String
        for(i in 1 .. text.length){
            temp = text.substring(nextLineStart,i)
            if(paint.measureText(temp) > availableWidth){
                // TODO: 2020/12/5  bug 
                nextLineStart = i
            }
        }
        return nextLineStart
    }

    /**
     * 从后向前计算上一页begin position
     */
    private fun findLastPageBegin(currentPageBegin: Int): Int{
        val lineCount = measureLineCount(config,screen)
        var paragraph = ""
        var textIndex = 0
        var mPosition = currentPageBegin
        for (i in 0 until lineCount){
            if(textIndex == 0){
                val lastParagraphStartPos = findLastParagraphStartPos(mPosition)
                //已经到文件首部
                if(lastParagraphStartPos == 0){
                    mPosition = 0
                    break
                }
                paragraph = readParagraphString(lastParagraphStartPos,mPosition)
                textIndex = paragraph.length
                mPosition = lastParagraphStartPos
            }
            textIndex = measureLineIndexBackward(paragraph.substring(0,textIndex),config,screen)
        }
        if(textIndex != 0){
            mPosition += paragraph.substring(0,textIndex).toByteArray(Charset.forName(book.encode)).size
        }
        return mPosition
    }


    private fun compose(start: Int): Page{
        val lineCount = measureLineCount(config,screen)
        val lines = mutableListOf<String>()
        var paragraph = ""
        var mPosition = start
        for(i in 0 until lineCount){
            if(paragraph.isEmpty()){
                val newStart = findNextParagraphStartPos(mPosition)
                if(newStart == -1){
                    return Page(lines,mPosition)
                }
                paragraph = readParagraphString(mPosition,newStart)
                mPosition = newStart
            }
            val lineEnd = measureLineIndexForward(paragraph,screen)
            lines.add(paragraph.substring(0,lineEnd))
            paragraph = paragraph.substring(lineEnd,paragraph.length)
        }
        val offsetCorrection = paragraph.toByteArray(Charset.forName(book.encode)).size
        mPosition -= offsetCorrection
        return Page(lines,mPosition)
    }


    private fun generatePaint(config: PrintConfig): Paint{
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = config.textSize
        }
    }
    private  data class Page(
        val lines: List<String>,
        val currentPosition: Int
    )
}