package com.will.reader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.io.Serializable
import java.util.*

/**
 * created  by will on 2020/11/22 10:33
 */
@Entity(tableName = "book")
data class Book(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val name: String,
        val path: String,
        val encode: String,
        val brief: String,
        val size: Long,
        val charCount: Int,
        val readProgressInByte: Int,
        val lastReadTime: Long,
        val lastReadParagraph: String
): Serializable {
        companion object{
                fun build(path: String): Book{
                        return build(File(path))
                }
                fun build(file: File): Book{
                        val name = file.name
                        val path = file.path
                        val encode = "GB18030"
                        val brief = ""
                        val size = file.length()
                        val charCount = 0
                        val readProgressInByte = 0
                        val lastReadTime = 0L
                        val lastReadParagraph = ""
                        return Book(
                                name = name,
                                path = path,
                                encode = encode,
                                brief = brief,
                                size = size,
                                charCount = charCount,
                                readProgressInByte = readProgressInByte,
                                lastReadTime = lastReadTime,
                                lastReadParagraph = lastReadParagraph
                        )
                }
        }
}