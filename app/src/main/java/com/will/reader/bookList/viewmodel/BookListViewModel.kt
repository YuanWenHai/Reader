package com.will.reader.bookList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.will.reader.data.BookDao
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book
import kotlinx.coroutines.launch
import java.io.File

/**
 * created  by will on 2020/11/22 16:41
 */
class BookListViewModel(private val bookRepos: BookRepository,private val chapterRepos: ChapterRepository): ViewModel() {
    val bookFlow = Pager(PagingConfig(pageSize = 20)){
        bookRepos.bookInPaging()
    }.flow

    /**
     * 检查该文件是否存在,有修改编辑则更新数据库
     */
    fun checkBook(book: Book): Boolean{
        val bookFile = File(book.path)
        if(bookFile.isFile){
            if(book.size != bookFile.length()){
                viewModelScope.launch {
                    bookRepos.updateBook(book)
                }
            }
        }
        return bookFile.isFile
    }
    fun deleteBook(book: Book){
        viewModelScope.launch {
            bookRepos.deleteBook(book)
            chapterRepos.deleteChapterByBook(book)
        }

    }

}