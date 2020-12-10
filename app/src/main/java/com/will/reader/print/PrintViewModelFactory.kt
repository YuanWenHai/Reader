package com.will.reader.print

import android.util.DisplayMetrics
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.will.reader.data.BookRepository
import com.will.reader.data.model.Book

/**
 * created  by will on 2020/12/4 16:50
 */
class PrintViewModelFactory(private val bookRepository: BookRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PrintViewModel(bookRepository) as T
    }
}