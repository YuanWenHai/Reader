package com.will.reader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * created  by will on 2020/11/22 10:41
 */
@Entity(tableName = "chapter")
data class Chapter(
    @PrimaryKey
    val id: Long,
    val bookId: Int,
    val name: String,
    val number: Int,
    val positionInByte: Int,
    val charCount: Int
) {
    companion object{
        fun build(name: String, number: Int,positionInByte: Int,charCount: Int,bookId: Int): Chapter{
            val id = System.currentTimeMillis()
            // TODO: 2020/12/11 sync?
        }
    }
}