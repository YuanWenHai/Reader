package com.will.reader.bookList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.will.reader.data.BookDao

/**
 * created  by will on 2020/11/22 16:48
 */
class BookViewModelFactory(private val dao: BookDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BookListViewModel(dao) as T
    }
}