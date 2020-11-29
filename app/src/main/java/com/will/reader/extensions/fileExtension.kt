package com.will.reader.extensions

import java.io.File
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

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
fun File.toFormattedSize(): String{
    val size = length()
    val units = arrayOf("Byte","Kb","Mb","Gb","Tb")
    val base = log10(1024.0)
    val logged = log10(size.toDouble())
    val index = (logged/base).toInt()
    val result = size/(1024.0.pow(index))
    val str = "%.2f".format(result)
    return "$str ${units[index]}"
}