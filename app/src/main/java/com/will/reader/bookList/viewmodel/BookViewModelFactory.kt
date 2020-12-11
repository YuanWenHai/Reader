package com.will.reader.bookList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.will.reader.data.BookDao
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository

/**
 * created  by will on 2020/11/22 16:48
 */
class BookViewModelFactory(private val bookRepository: BookRepository, private val chapterRepository: ChapterRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BookListViewModel(bookRepository,chapterRepository) as T
    }
}