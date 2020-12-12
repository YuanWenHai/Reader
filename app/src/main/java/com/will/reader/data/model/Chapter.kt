package com.will.reader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * created  by will on 2020/11/22 10:41
 */
@Entity(tableName = "chapter")
data class Chapter(
    @PrimaryKey
    val id: Long,
    val bookId: Long,
    val name: String,
    val number: Int,
    val positionInByte: Int,
    val charCount: Int
) {
    companion object{
        fun build(name: String, number: Int, positionInByte: Int, charCount: Int, bookId: Long): Chapter{
            val id = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
            return Chapter(id,name = name,number = number,positionInByte = positionInByte,charCount = charCount,bookId =  bookId)
        }
    }
}