package com.will.reader.bookList.util

import kotlin.math.log10
import kotlin.math.pow

/**
 * created  by will on 2020/11/22 18:37
 */
class Util {

    companion object{

        fun byteSizeToFormattedString(size: Long): String{
            val units = arrayOf("Byte","Kb","Mb","Gb","Tb")
            val base = log10(1024.0)
            val logged = log10(size.toDouble())
            val index = (logged/base).toInt()
            val result = size/(1024.0.pow(index))
            val str = "%.2f".format(result)
            return "$str ${units[index]}"

        }
    }
}