package com.will.reader.print

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.will.reader.data.BookDao
import com.will.reader.data.model.Book

/**
 * created  by will on 2020/12/3 12:51
 */
class PrintViewModel(val bookDao: BookDao): ViewModel() {
    private val printConfig: MutableLiveData<PrintConfig> = MutableLiveData()

    fun updateBook(book: Book){
        bookDao.updateBook(book)
    }

}