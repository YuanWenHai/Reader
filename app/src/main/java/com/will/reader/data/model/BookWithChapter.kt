package com.will.reader.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * created  by will on 2020/11/22 10:47
 */
data class BookWithChapter(
    @Embedded val book: Book,
    @Relation(parentColumn = "id",entityColumn = "bookId") val chapters: List<Chapter>
)
{
}