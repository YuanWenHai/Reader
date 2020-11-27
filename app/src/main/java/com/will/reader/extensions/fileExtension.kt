package com.will.reader.extensions

import java.io.File
import java.util.*

/**
 * created  by will on 2020/11/27 16:01
 */
fun File.isBook(): Boolean{
    return suffix() == "TXT" || suffix() == "TEXT"
}

fun File.suffix(): String{
    return with(name){
        substring(lastIndexOf(".") + 1, length).toUpperCase(Locale.ROOT)
    }
}