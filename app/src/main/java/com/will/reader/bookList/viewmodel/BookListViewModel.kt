package com.will.reader.bookList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.will.reader.data.BookDao
import com.will.reader.data.model.Book

/**
 * created  by will on 2020/11/22 16:41
 */
class BookListViewModel(dao: BookDao): ViewModel() {
    val bookFlow = Pager(PagingConfig(pageSize = 20)){
        dao.getAllBookInPage()
    }.flow
}