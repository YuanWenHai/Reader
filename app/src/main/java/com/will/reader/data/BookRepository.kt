package com.will.reader.data

import androidx.paging.PagingSource
import com.will.reader.data.model.Book
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

/**
 * created  by will on 2020/11/27 16:43
 */
class BookRepository private constructor(private val bookDao: BookDao) {

    suspend fun saveBook(book: Book){
        withContext(IO){
            if(ifBookNotExist(book)){
                bookDao.saveBook(book)
            }
        }
    }

    suspend fun saveBook(bookList: List<Book>){
        withContext(IO){
            val f = bookList.filter {
                ifBookNotExist(it)
            }
            bookDao.saveBook(f)
        }
    }
    private fun ifBookNotExist(book: Book): Boolean{
        return bookDao.getBookByPath(book.path) == null
    }

    suspend fun updateBook(book: Book){
        withContext(IO){
            bookDao.updateBook(book)
        }
    }

    suspend fun deleteBook(book: Book){
        withContext(IO){
            bookDao.deleteBook(book)
        }
    }
    fun bookInPaging(): PagingSource<Int,Book>{
        return bookDao.getAllBookInPage()
    }

    companion object {
        private var instance: BookRepository? = null

        fun getInstance(bookDao: BookDao): BookRepository{
            return instance ?: synchronized(this){
                instance ?: BookRepository(bookDao).also { instance = it }
            }
        }
    }
}