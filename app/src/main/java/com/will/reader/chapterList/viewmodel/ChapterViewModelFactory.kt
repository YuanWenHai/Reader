package com.will.reader.chapterList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book

/**
 * created  by will on 2020/12/11 17:08
 */
class ChapterListViewModelFactory(private val book: Book,private val chapterRepos: ChapterRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChapterListViewModel(book,chapterRepos) as T
    }

}