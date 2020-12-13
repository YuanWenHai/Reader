package com.will.reader.chapterList

import com.will.reader.data.model.Book
import com.will.reader.data.model.Chapter
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

/**
 * created  by will on 2020/12/11 17:49
 */
class ChapterFinder(private val book: Book) {
    private val regex: Regex = Regex("第[(0-9)零一二三四五六七八九十百千万]*章.*")



    suspend fun indexing(keyword: String): Flow<FindResult> = flow{
        val bookFile = File(book.path)
        val regex = Regex("第[(0-9)零一二三四五六七八九十百千万]*${keyword}.*")
        if(bookFile.isFile){
            withContext(IO){
                val fi = FileInputStream(bookFile)
                val channel = fi.channel
                val bytes = channel.map(FileChannel.MapMode.READ_ONLY,0,bookFile.length())
                var number = 0
                var charCount = 0
                var currentPos = 0
                var current: Chapter
                var previous: Chapter? = null
                var emittedBytes = 0
                val emitThreshold = 1024 * 10 //每10kb提交一次progress
                while (currentPos < bookFile.length()){
                    val line = readLine(currentPos,bytes)
                    charCount += line.text.length
                    if(regex.matches(line.text)){
                        current = Chapter.build(line.text,number,currentPos,0,book.id)
                        previous?.let {
                            emit(FindResult.Find(it.copy(charCount = charCount)))
                            charCount = 0
                        }
                        previous = current
                        number++
                    }
                    emittedBytes += line.nextLineStart - currentPos
                    if(emittedBytes > emitThreshold){
                        emit(FindResult.Progress(currentPos*100/bookFile.length().toInt()))
                        emittedBytes = 0
                    }
                    currentPos = line.nextLineStart
                }
                previous?.let {
                    emit(FindResult.Find(it.copy(charCount = charCount)))
                }
                emit(FindResult.Finish)
                channel.close()
                fi.close()
            }
        }
    }.flowOn(IO)



    suspend fun find(){
        val bookFile = File(book.path)
        if(!bookFile.isFile){
            return
        }
        withContext(IO){

            val fi = FileInputStream(bookFile)
            val channel = fi.channel
            val bytes = channel.map(FileChannel.MapMode.READ_ONLY,0,bookFile.length())
            val chapters = mutableListOf<Chapter>()
            var number = 0
            var charCount = 0
            var currentPos = 0
            while (currentPos < bookFile.length()){
                val line = readLine(currentPos,bytes)
                charCount += line.text.length
                if(regex.matches(line.text)){
                    val chapter = Chapter.build(line.text,number,currentPos,0,book.id)
                    if(chapters.isNotEmpty()){
                        val c = chapters.last()
                        chapters[chapters.lastIndex] = c.copy(charCount = charCount)
                        charCount = 0
                    }
                    chapters.add(chapter)
                    number++
                }
                currentPos = line.nextLineStart
            }
            if(chapters.isNotEmpty()){
                val c = chapters.last()
                chapters[chapters.lastIndex] = c.copy(charCount = charCount)
            }
            channel.close()
            fi.close()
        }

    }

    private fun readLine(startPos: Int,bytes: MappedByteBuffer): Line{
        val lf = '\n'.toByte()
        var newStart = 0
        for(i in startPos until bytes.limit()){
            if (bytes[i] == lf){
                newStart = i+1
                break
            }
            newStart = bytes.limit()
        }
        val temp = ByteArray(newStart - startPos)
        for( i in temp.indices){
            temp[i] = bytes[startPos+i]
        }
        val text = String(temp, Charset.forName(book.encode)).replace("\r","").replace("\n","")
        return Line(text,newStart)
    }


    private data class Line(
        val text: String,
        val nextLineStart: Int
    )

    sealed class FindResult{
        data class Find(val chapter: Chapter): FindResult()
        data class Progress(val progress: Int): FindResult()
        object Finish : FindResult()
    }
}