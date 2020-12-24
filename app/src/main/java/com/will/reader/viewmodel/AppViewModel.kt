package com.will.reader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/12/20 11:32
 */
class AppViewModel(private val bookRepos: BookRepository,private val chapterRepos: ChapterRepository): ViewModel() {
    private val currentBook: MutableLiveData<Book> = MutableLiveData()
    fun book(): LiveData<Book> = currentBook


    fun updateBook(book: Book){
        currentBook.value = book
        viewModelScope.launch {
            bookRepos.updateBook(book)
        }
    }
    fun deleteBook(book: Book){
        viewModelScope.launch {
            bookRepos.deleteBook(book)
            chapterRepos.deleteChapterByBook(book)
        }
    }
}