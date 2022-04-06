package com.will.reader.bookList.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.will.reader.data.BookDao
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book
import com.will.reader.extensions.isBook
import com.will.reader.util.LOG_TAG
import kotlinx.coroutines.launch
import java.io.File

/**
 * created  by will on 2020/11/22 16:41
 */
class BookListViewModel(private val bookRepos: BookRepository,private val chapterRepos: ChapterRepository): ViewModel() {
    val bookFlow = Pager(PagingConfig(pageSize = 20)){
        bookRepos.bookInPaging()
    }.flow

}