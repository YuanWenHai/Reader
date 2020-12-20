package com.will.reader.print

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book

/**
 * created  by will on 2020/12/4 16:50
 */
class PrintViewModelFactory(private val book: Book,private val chapterRepository: ChapterRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReaderViewModel(book,chapterRepository) as T
    }
}