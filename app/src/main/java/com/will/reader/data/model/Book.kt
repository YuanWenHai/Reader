package com.will.reader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * created  by will on 2020/11/22 10:33
 */
@Entity(tableName = "book")
data class Book(
        @PrimaryKey
        val id: Int,
        val name: String,
        val path: String,
        val encode: String,
        val brief: String,
        val size: Int,
        val charCount: Int,
        val readProgressInByte: Int,
        val lastReadTime: Int,
        val lastReadParagraph: String
) {

}