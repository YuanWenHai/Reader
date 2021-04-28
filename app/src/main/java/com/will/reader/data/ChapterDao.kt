package com.will.reader.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.will.reader.data.model.Chapter

/**
 * created  by will on 2020/11/22 11:17
 */
@Dao
interface ChapterDao {



    @Query("SELECT * FROM chapter WHERE id=:id")
    fun getChapterById(id: Long): Chapter

    @Query("SELECT * FROM chapter WHERE (bookId= :bookId AND positionInByte < :position)  ORDER BY positionInByte DESC LIMIT 1 ")
    fun getChapterByBookIdAndPositionRange(bookId: Long,position: Int): Chapter?

    @Query("SELECT * FROM chapter WHERE bookId = :id ORDER BY number ASC")
    fun getChaptersPagingByBookId(id: Long): PagingSource<Int,Chapter>

    @Query("SELECT * FROM  chapter WHERE bookId = :id ORDER BY number ASC")
    fun getAllChapters(id: Long): List<Chapter>

    @Insert
    fun saveChapter(chapter: Chapter)

    @Insert
    fun saveChapter(chapterList: List<Chapter>)

    @Delete
    fun deleteChapter(chapter: Chapter)

    @Query("DELETE FROM chapter WHERE bookId = :bookId")
    fun deleteChaptersByBookId(bookId: Long)
}