package com.will.reader.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.will.reader.data.model.Chapter

/**
 * created  by will on 2020/11/22 11:17
 */
@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapter")
    fun getChapterInPage(): PagingSource<Int,Chapter>

    @Query("SELECT * FROM chapter WHERE id=:id")
    fun getChapterById(id: Long): Chapter

    @Insert
    fun saveChapter(chapter: Chapter)

    @Insert
    fun saveChapter(chapterList: List<Chapter>)
}