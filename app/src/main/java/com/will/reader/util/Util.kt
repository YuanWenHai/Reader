package com.will.reader.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

/**
 * created  by will on 2020/11/27 16:23
 */

fun makeToast(context: Context,content: String){
    Toast.makeText(context,content,Toast.LENGTH_SHORT).show()
}
fun makeLongToast(context: Context,content: String){
    Toast.makeText(context,content,Toast.LENGTH_LONG).show()
}
const val LOG_TAG = "!~"
fun logW(content: String){
    Log.w(LOG_TAG,content)
}
fun logE(content: String){
    Log.e(LOG_TAG,content)
}
fun byteSizeToFormattedString(size: Long): String{
    val units = arrayOf("Byte","Kb","Mb","Gb","Tb")
    val base = log10(1024.0)
    val logged = log10(size.toDouble())
    val index = (logged/base).toInt()
    val result = size/(1024.0.pow(index))
    val str = "%.2f".format(result)
    return "$str ${units[index]}"

}
fun getFormattedTime(pattern: String): String{
    val formatter = SimpleDateFormat(pattern, Locale.CHINA)
    return formatter.format(Date(System.currentTimeMillis()))
}