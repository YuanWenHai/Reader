package com.will.reader.data

import androidx.paging.PagingSource
import androidx.room.*
import com.will.reader.data.model.Book

/**
 * created  by will on 2020/11/22 10:50
 */
@Dao
interface BookDao {

    @Query("SELECT * FROM book")
    fun getAllBookInPage(): PagingSource<Int,Book>

    @Query("SELECT * FROM book WHERE id= :id")
    fun getBookById(id: Long): Book

    @Query("SELECT * FROM book WHERE path= :path" )
    fun getBookByPath(path: String): Book?

    @Insert
    fun saveBook(book: Book)

    @Insert
    fun saveBook(bookList: List<Book>)

    @Update
    fun updateBook(book: Book)

    @Delete
    fun deleteBook(book: Book)
}