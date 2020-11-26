package com.will.reader.scan

import com.will.reader.bookList.util.Util

/**
 * created  by will on 2020/11/22 17:17
 */
data class FileItem(
    val name: String,
    val size: String,
    val path: String,
    val selected: Boolean
) {

}