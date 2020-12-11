package com.will.reader.chapterList

import com.will.reader.data.model.Book
import com.will.reader.data.model.Chapter
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

/**
 * created  by will on 2020/12/11 17:49
 */
class ChapterFinder(private val book: Book) {
    private val regex: Regex = Regex("第[0-9]*章*")

    init {

    }

    fun find(){
        val bookFile = File(book.path)
        if(!bookFile.isFile){
            return
        }
        val fi = FileInputStream(bookFile)
        val bytes = fi.channel.map(FileChannel.MapMode.READ_ONLY,bookFile.length(),bookFile.length())
        val chapters = mutableListOf<Chapter>()
        var currentPos = 0
        while (currentPos < bookFile.length()){
            val line = readLine(currentPos,bytes)
            if(regex.matches(line.text)){
                val chapter = Chapter()
            }
        }

    }

    private fun readLine(startPos: Int,bytes: MappedByteBuffer): Line{
        val lf = "\n".toByte()
        var lineEnd = 0
        for(i in startPos until bytes.limit()){
            if (bytes[i] == lf){
                lineEnd = i
                break
            }
        }
        val temp = ByteArray(lineEnd - startPos)
        for( i in temp.indices){
            temp[i] = bytes[startPos+i]
        }
        val text = String(temp, Charset.forName(book.encode))
        return Line(text,lineEnd+1)
    }


    private data class Line(
        val text: String,
        val nextLineStart: Int
    )
}