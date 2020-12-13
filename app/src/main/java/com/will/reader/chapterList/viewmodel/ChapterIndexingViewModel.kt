package com.will.reader.chapterList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Chapter
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/12/13 16:32
 */
class ChapterIndexingViewModel(private val chapterRepository: ChapterRepository): ViewModel() {

    private val chapters = mutableListOf<Chapter>()


    fun addChapter(chapter: Chapter){
        chapters.add(chapter)
    }

    fun commit(){
        viewModelScope.launch {
            chapterRepository.add(chapters)
        }
    }
}