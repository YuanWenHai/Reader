package com.will.reader.print

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
    private var paragraphEnd = 0
    init {
        val file = File(book.path)
        byteChannel = RandomAccessFile(file,"r").channel
        bookBytes = byteChannel.map(FileChannel.MapMode.READ_ONLY,0,file.length())
    }
    private fun readParagraphString(start: Int,end: Int): String{
        val bytes = ByteArray(end-start)
        bookBytes.get(bytes)
        return String(bytes,Charset.forName(book.encode))
    }
    private fun findNextParagraphEndPos(lastEnd: Int): Int{
        val lf = 10.toByte()
        val cr = 13.toByte()
        if(lastEnd == bookBytes.limit()){
            //reach the end
            return -1
        }
        // TODO: 2020/11/29 这里会无视掉判断？ 
        for(i in lastEnd until bookBytes.limit()){
            if(i != lastEnd && bookBytes[i] == lf){
                return lastEnd + i
            }
        }
        return bookBytes.limit()
    }

    private fun measureLineCount(config: PrintConfig,screen: DisplayMetrics): Int{
        val availableHeight = screen.heightPixels - config.textMarginTop - config.textMarginBottom - config.bottomBarHeight
        val lineHeight = config.textSize + config.textLineSpace
        return (availableHeight/lineHeight).toInt()
    }

    /**
     * return line end index exclusive
     */
    private fun measureLineIndex(text: String, config: PrintConfig, screen: DisplayMetrics): Int{
        val availableWidth = screen.widthPixels - config.textMarginStart - config.textMarginEnd
        paint.textSize = config.textSize
        for( i in 1 until text.length){
            if(paint.measureText(text.substring(0,i)) > availableWidth){
                return i
            }
        }
        return text.length
    }
    fun fill(): List<String>{
        val lineCount = measureLineCount(config,screen)
        val lines = mutableListOf<String>()
        var paragraph = ""
        pageBegin = paragraphEnd
        for(i in 0 until lineCount){
            if(paragraph.isEmpty()){
                val newEnd = findNextParagraphEndPos(paragraphEnd)
                if(newEnd == -1){
                    return lines
                }
                paragraph = readParagraphString(paragraphEnd,newEnd)
                paragraphEnd = newEnd
            }
            val lineEnd = measureLineIndex(paragraph,config,screen)
            lines.add(paragraph.substring(0,lineEnd))
            paragraph = paragraph.substring(lineEnd,paragraph.length)
        }
        return lines
    }
}