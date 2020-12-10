package com.will.reader.print

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.os.BatteryManager
import android.util.DisplayMetrics
import com.will.reader.data.model.Book
import com.will.reader.util.getFormattedTime
import java.io.File
import java.io.RandomAccessFile
import java.lang.StringBuilder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import kotlin.math.roundToInt

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
     *//*
    fun setConfig(config: PrintConfig){
        this.config = config
        this.paint = generatePaint(config)
        prepare()
    }*/
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

    fun printWithNewConfig(context: Context,config: PrintConfig): PrinterPage{
        this.config = config
        this.paint = generatePaint(config)
        return print(context)
    }

    /**
     * 从当前page begin位置开始排版一页内容，并将current position 移动到下一页首个byte
     */
    fun print(context: Context): PrinterPage{
        val page = compose(pageBegin)
        currentPosition = page.currentPosition
        lines = page.lines
        return generatePrinterPage(context,page)
    }

    /**
     * 从下一页首个byte位置开始排版一页内容，并将current position移动到排版后的下一页首个byte
     */
    fun pageDown(context: Context): PrinterPage?{
        if(currentPosition >= bookBytes.limit()){
            return null
        }
        pageBegin = currentPosition
        val page = compose(currentPosition)
        currentPosition = page.currentPosition
        lines = page.lines
        return generatePrinterPage(context,page)
    }

    /**
     *从当前page begin位置上翻一页排版一页内容，并将page begin移动到当前页首个byte，current position为下一页首个byte
     */
    fun pageUp(context: Context): PrinterPage? {
        if(pageBegin == 0){
            return null
        }
        val start = findLastPageBegin(pageBegin)
        val page = compose(start)
        pageBegin = start
        currentPosition = page.currentPosition
        lines = page.lines
        return generatePrinterPage(context,page)
    }

    fun draw(canvas: Canvas,context: Context){
        canvas.drawColor(config.backgroundColor)
        lines.forEachIndexed{
                index, line ->
            val x = config.textMarginStart
            val y = config.textMarginTop + ((index+1)*(config.textSize+config.textLineSpace))
            canvas.drawText(line,x,y,paint)
        }
        drawBottomBar(canvas,context)
    }

    private fun drawBottomBar(canvas: Canvas,context: Context){
        val bottomBarTextSize = config.bottomBarHeight * 0.66f
        val y = screen.heightPixels - (config.bottomBarHeight * 0.34f)

        val timeText = "\uD83D\uDD52${getFormattedTime("HH:mm")}"
        val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context.registerReceiver(null,it)
        }
        val batteryLevelText = batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE,-1)
            val percentage = level*100/scale.toFloat()
            "\uD83D\uDD0B${percentage.roundToInt()}%"
        } ?: ""
        val progress = currentPosition*100/bookFile.length().toFloat()
        val progressText = "%.3f".format(progress).plus("%")
        paint.textSize = bottomBarTextSize
        //draw battery level text
        canvas.drawText(batteryLevelText,config.textMarginStart,y,paint)
        //draw progress text
        val progressX = (screen.widthPixels - paint.measureText(progressText)) * 0.5f
        canvas.drawText(progressText,progressX,y,paint)
        //draw time text
        val timeX = screen.widthPixels - config.textMarginEnd - paint.measureText(timeText)
        canvas.drawText(timeText,timeX,y,paint)
        paint.textSize = config.textSize

    }

    private fun generatePrinterPage(context: Context,page: Page): PrinterPage{
        val timeText = "\uD83D\uDD52${getFormattedTime("HH:mm")}"
        val progress = page.currentPosition*100/bookFile.length().toFloat()
        val progressText = "%.3f".format(progress).plus("%")
        val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context.registerReceiver(null,it)
        }
        val batteryLevelText = batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE,-1)
            val percentage = level*100/scale.toFloat()
            "\uD83D\uDD0B${percentage.roundToInt()}%"
        } ?: ""
        return PrinterPage(page.lines,batteryText = batteryLevelText,progressText = progressText,timeText = timeText)
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
     *寻找下一段末尾(/n)的byte位置,若已到文件末尾则返回-1
     * @param currentStart inclusive
     */
    private fun findNextParagraphStartPos(currentStart: Int): Int{
        val lf = 10.toByte()
        val cr = 13.toByte()
        if(currentStart >= bookBytes.limit()){
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

    /**
     * 在bytes文件当中根据换行符按序查询上一段落的起始byte position
     * @return 上一段的起始byte position
     */
    private fun findLastParagraphStartPos(currentStart: Int): Int{
        if(currentStart == 0){
            return 0
        }
        val start = currentStart - 1
        val lf = 10.toByte() // \n
        val cr = 13.toByte() // \r
        for(i in start downTo 0){
            if(i != start && bookBytes[i] == lf){
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
     * 正序测量该行尾Index，returned index is exclusive
     * @return 第一行 end position
     */
    private fun measureLineIndexForward(text: String,  screen: DisplayMetrics): Int{
        val availableWidth = screen.widthPixels - config.textMarginStart - config.textMarginEnd
        for( i in 1 until text.length){
            //测量行长度时，忽略cr与lf，避免当\r\n出现在行尾时发生拆分，导致排版多出一行的问题
            if(text[i-1] == '\r' || text[i-1] == '\n'){
                continue
            }
            if(paint.measureText(text.substring(0,i)) > availableWidth){
                return i - 1
            }
        }
        return text.length
    }

    /**
     * 倒序测量最后一行起始index
     *
     * ## 注意这里的代码逻辑
     *
     * 虽然是倒序测量，但并非literally从后向前测量，而是从前向后测量每行直到获得最后一行起始位置
     *
     * 这里还忽略了lf与cr的measure长度，是为了避免错误的拆分
     *
     * 这是为了保持前后翻页排版的一致性。
     * @return 最后一行start index,若单行可以填充则返回0
     */
    private fun measureLineIndexBackward(text: String,config: PrintConfig,screen: DisplayMetrics): Int{
        if(text.isEmpty()){
            return 0
        }
        val availableWidth = screen.widthPixels - config.textMarginStart - config.textMarginEnd
        var lineStart = 0
        var temp: String
        for(i in 1 .. text.length){
            //测量行长度时，忽略cr与lf，避免当\r\n出现在行尾时发生拆分，导致排版多出一行的问题
            if(text[i-1] == '\r' || text[i-1] == '\n'){
                continue
            }
            temp = text.substring(lineStart,i)
            if(paint.measureText(temp) > availableWidth){
                lineStart = i - 1
            }
        }
        return lineStart
    }

    /**
     * 从后向前计算上一页begin position，printer的核心方法之一
     * @return 上一页起始position
     */
    private fun findLastPageBegin(currentPageBegin: Int): Int{
        val lineCount = measureLineCount(config,screen)
        //段落string
        var paragraph = ""
        //每行使用段落的index
        var textIndex = 0
        //byte position
        var mPosition = currentPageBegin
        //循环需要的行数填充
        for (i in 0 until lineCount){
            //若textIndex为0则说明本段paragraph已经打印完毕，再向前读取一个paragraph
            if(textIndex == 0){
                val lastParagraphStartPos = findLastParagraphStartPos(mPosition)
                //已经到文件首部，直接返回0
                if(lastParagraphStartPos == 0){
                    return 0
                }
                //将bytes读为string，使用book中的encode
                paragraph = readParagraphString(lastParagraphStartPos,mPosition)
                //移动mPosition到当前位置
                mPosition = lastParagraphStartPos
            }
            //测量当前屏幕参数中一行所需要的字数，因为是上翻页，所以这里的textIndex是最后一行的起始index
            textIndex = measureLineIndexBackward(paragraph,config,screen)
            //paragraph"打印"消耗了这段文字，剩余的paragraph进入下一循环进行打印
            paragraph = paragraph.substring(0, textIndex)
        }
        //当所有行都填充完毕后，若textIndex不为0则说明paragraph未全部使用，需要在byte position中补正
        if(textIndex != 0){
            //方式很简单，string encoding为byte，依旧使用book中的encode
            mPosition += paragraph.substring(0,textIndex).toByteArray(Charset.forName(book.encode)).size
        }
        //返回计算出的Position
        return mPosition
    }


    /**
     * 从前向后阅读一页，printer的核心方法之一
     * @return Page,包含已读取的页面内容以及读取本页后的byte position
     */
    private fun compose(start: Int): Page{
        //获取页面行数
        val lineCount = measureLineCount(config,screen)
        val lines = mutableListOf<String>()
        //段落string
        var paragraph = ""
        //byte position
        var mPosition = start
        //循环填充
        for(i in 0 until lineCount){
            //若paragraph为空，则向后读取一个byte paragraph并读为string
            if(paragraph.isEmpty()){
                val newStart = findNextParagraphStartPos(mPosition)
                //若newStart为-1，则说明当前mPosition已到达文件limit，直接返回已有内容
                if(newStart == -1){
                    return Page(lines,mPosition)
                }
                paragraph = readParagraphString(mPosition,newStart)
                //byte position后移
                mPosition = newStart
            }
            //测量本行需要使用paragraph中多少字
            val lineEnd = measureLineIndexForward(paragraph,screen)
            lines.add(paragraph.substring(0,lineEnd))
            //更新paragraph，因为“使用”了一行
            paragraph = paragraph.substring(lineEnd,paragraph.length)
        }
        //若行排版完毕paragraph仍有剩余，则需要byte position补正
        if(paragraph.isNotEmpty()){
            val offsetCorrection = paragraph.toByteArray(Charset.forName(book.encode)).size
            mPosition -= offsetCorrection
        }

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
    data class PrinterPage(
        val lines: List<String>,
        val batteryText: String,
        val progressText: String,
        val timeText: String
    )
}