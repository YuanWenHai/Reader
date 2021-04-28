package com.will.reader.chapterList.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.will.reader.data.ChapterDao
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book
import com.will.reader.data.model.Chapter
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/12/11 16:45
 */
class ChapterListViewModel(private val book: Book, private val chapterRepos: ChapterRepository): ViewModel() {
    private val allChapters = MutableLiveData<List<Chapter>>()

    private val currentIndex = MutableLiveData<Int>()

    fun start(){
        viewModelScope.launch {
            allChapters.value = chapterRepos.getAllChapters(book)
            val chapter = chapterRepos.getChapterByBookAndPositionRange(book,book.readProgressInByte)
            currentIndex.value = chapter?.number ?: 0
        }
    }



    fun deleteAllChapter(){
        viewModelScope.launch {
            chapterRepos.deleteChapterByBook(book)
        }
    }
    fun getCurrentIndex(): LiveData<Int>{
        return currentIndex
    }
    fun getAllChapters(): LiveData<List<Chapter>>{
        return allChapters
    }
}