package com.will.reader.chapterList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.will.reader.data.ChapterDao
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/12/11 16:45
 */
class ChapterListViewModel(private val book: Book, private val chapterRepos: ChapterRepository): ViewModel() {
    val chapterFlow = Pager(PagingConfig(pageSize = 200)){
        chapterRepos.getChaptersPagingByBook(book)
    }.flow

    fun deleteAllChapter(){
        viewModelScope.launch {
            chapterRepos.deleteChapterByBook(book)
        }
    }
}