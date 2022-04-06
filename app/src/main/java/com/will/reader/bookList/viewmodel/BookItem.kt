package com.will.reader.bookList.viewmodel

import com.will.reader.data.model.Book
import com.will.reader.util.byteSizeToFormattedString
import java.text.SimpleDateFormat
import java.util.*

/**
 * created  by will on 2020/11/22 16:17
 */
data class BookItem(
    val name: String,
    val brief: String,
    val size: String,
    val lastReadTime: String,
    val progress: String
) {
    companion object{
        fun build(book: Book): BookItem{
            val progressPercent = if(book.readProgressInByte == 0) 0.0 else (book.readProgressInByte.toDouble()/book.size.toDouble()) * 100
            val formattedSize = "${byteSizeToFormattedString(book.size)}，已阅读%.2f".format(progressPercent).plus("%")
            val lastReadTime = if(book.lastReadTime == 0L) "尚未开始阅读" else SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date(book.lastReadTime))
            val progress = "${byteSizeToFormattedString(book.size)}，已阅读%.2f".format(progressPercent).plus("%")
            return BookItem(
                    book.name, book.brief,formattedSize,lastReadTime,progress)
        }
    }
}