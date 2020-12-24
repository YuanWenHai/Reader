package com.will.reader.reader

import android.graphics.Paint
import com.will.reader.data.model.Book
import java.io.File
import java.io.RandomAccessFile
import java.lang.StringBuilder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import kotlin.math.max
import kotlin.math.min

/**
 * created  by will on 2020/11/29 11:43
 */
class Printer(private var book: Book){
    private val bookFile = File(book.path)
    private val raf: RandomAccessFile
    private val bookBytes: MappedByteBuffer
    private val byteChannel: FileChannel
    private var pageBegin = book.readProgressInByte
    private var currentPosition = 0
    private var currentPage = Page(emptyList(),0)
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    init {
        val file = File(book.path)
        raf = RandomAccessFile(file,"r")
        byteChannel = raf.channel
        bookBytes = byteChannel.map(FileChannel.MapMode.READ_ONLY,0,file.length())
    }

    fun closeBook(){
        byteChannel.close()
        raf.close()
    }


    fun getCurrentBookStateForSave(): Book{
        val readProgressInByte = pageBegin
        val builder = StringBuilder()
        currentPage.lines.forEach{builder.append(it)}
        val lastReadParagraph = builder.toString()
        val lastReadTime = System.currentTimeMillis()
        return book.copy(readProgressInByte = readProgressInByte,lastReadParagraph = lastReadParagraph,lastReadTime = lastReadTime)
    }

    fun getBook(): Book{
        return book;
    }

    fun setEncoding(encode: String){
        this.book = book.copy(encode = encode)
    }

    fun skipToProgress(progress: Float){
        val p = min(100f,max(0f,progress))/100
        val targetPos = (bookFile.length() * p).toInt()
        val availablePos = findLastParagraphStartPos(targetPos)
        pageBegin = availablePos
    }
    fun skipToPosition(position: Int){
        pageBegin = position
    }

    /**
     * 从当前page begin位置开始排版一页内容，并将current position 移动到下一页首个byte
     */
    fun print(config: Config): Page{
        val page = compose(pageBegin,config)
        currentPage = page
        currentPosition = page.position
        //这里使用当前页首的位置，而非下一页首的位置
        return page.copy(position = pageBegin)
    }

    /**
     * 从下一页首个byte位置开始排版一页内容，并将current position移动到排版后的下一页首个byte
     */
    fun pageDown(config: Config): Page{
        if(currentPosition >= bookBytes.limit()){
            return currentPage
        }
        val page = compose(currentPosition,config)
        currentPage = page
        pageBegin = currentPosition
        currentPosition = page.position
        return page.copy(position = pageBegin)
    }

    /**
     *从当前page begin位置上翻一页排版一页内容，并将page begin移动到当前页首个byte，current position为下一页首个byte
     */
    fun pageUp(config: Config): Page {
        if(pageBegin == 0){
            return currentPage.copy(position = 0)
        }
        val start = findLastPageBegin(pageBegin,config)
        val page = compose(start,config)
        currentPage = page
        pageBegin = start
        currentPosition = page.position
        return page.copy(position = pageBegin)
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

    private fun measureLineCount(config: Config): Int{
        val availableHeight = config.height - config.marginTop - config.marginBottom - config.bottomBarHeight
        val lineHeight = config.textSize + config.lineSpace
        return (availableHeight/lineHeight).toInt()
    }

    /**
     * 正序测量该行尾Index，returned index is exclusive
     * @return 第一行 end position
     */
    private fun measureLineIndexForward(text: String,config: Config): Int{
        val availableWidth = config.width - config.marginStart - config.marginEnd
        mPaint.textSize = config.textSize
        for( i in 1 until text.length){
            //测量行长度时，忽略cr与lf，避免当\r\n出现在行尾时发生拆分，导致排版多出一行的问题
            if(text[i-1] == '\r' || text[i-1] == '\n'){
                continue
            }
            if(mPaint.measureText(text.substring(0,i)) > availableWidth){
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
    private fun measureLineIndexBackward(text: String,config: Config): Int{
        if(text.isEmpty()){
            return 0
        }
        mPaint.textSize = config.textSize
        val availableWidth = config.width - config.marginStart - config.marginEnd
        var lineStart = 0
        var temp: String
        for(i in 1 .. text.length){
            //测量行长度时，忽略cr与lf，避免当\r\n出现在行尾时发生拆分，导致排版多出一行的问题
            if(text[i-1] == '\r' || text[i-1] == '\n'){
                continue
            }
            temp = text.substring(lineStart,i)
            if(mPaint.measureText(temp) > availableWidth){
                lineStart = i - 1
            }
        }
        return lineStart
    }

    /**
     * 从后向前计算上一页begin position，printer的核心方法之一
     * @return 上一页起始position
     */
    private fun findLastPageBegin(currentPageBegin: Int,config: Config): Int{
        val lineCount = measureLineCount(config)
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
            textIndex = measureLineIndexBackward(paragraph,config)
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
    private fun compose(start: Int,config: Config): Page{
        //获取页面行数
        val lineCount = measureLineCount(config)
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
            val lineEnd = measureLineIndexForward(paragraph,config)
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


    data class Page(
        val lines: List<String>,
        val position: Int,
    )
    data class Config(
        val bottomBarHeight: Float,
        val marginTop: Float,
        val marginBottom: Float,
        val marginStart: Float,
        val marginEnd: Float,
        val lineSpace: Float,
        val textSize: Float,
        val height: Float,
        val width: Float
    ){
        companion object{
            fun build(p: PrintConfig,width: Float,height: Float): Config{
                return Config(p.bottomBarHeight,p.textMarginTop,p.textMarginBottom,p.textMarginStart,p.textMarginEnd,p.textLineSpace,p.textSize,height,width)
            }
        }
    }
}