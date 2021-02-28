package com.will.reader.data

import androidx.paging.PagingSource
import com.will.reader.data.model.Book
import com.will.reader.data.model.Chapter
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

/**
 * created  by will on 2020/12/11 10:28
 */
class ChapterRepository private constructor (private val dao: ChapterDao) {

    suspend fun add(chapter: Chapter){
        withContext(IO){
            dao.saveChapter(chapter)
        }
    }

    suspend fun add(chapters: List<Chapter>){
        withContext(IO){
            dao.saveChapter(chapters)
        }
    }
    fun getChaptersPagingByBook(book: Book): PagingSource<Int,Chapter>{
        return dao.getChaptersPagingByBookId(book.id)
    }

    suspend fun getAllChapters(book: Book): List<Chapter>{
        return withContext(IO){
            dao.getAllChapters(book.id)
        }
    }


    suspend fun getChapterByBookAndPositionRange(book: Book, position: Int): Chapter?{
        if(position < 0){
            throw IllegalArgumentException("the position cannot be negative number")
        }
        return withContext(IO){
            dao.getChapterByBookIdAndPositionRange(book.id,position)
        }
    }
    suspend fun deleteChapterByBook(book: Book){
        withContext(IO){
            dao.deleteChaptersByBookId(book.id)
        }
    }

    companion object{
        private var instance: ChapterRepository? = null

        fun getInstance(dao: ChapterDao): ChapterRepository{
            return instance ?: synchronized(this){
                instance ?: ChapterRepository(dao).also { instance = it }
            }
        }
    }
}