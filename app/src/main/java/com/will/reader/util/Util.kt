package com.will.reader.util

import android.content.Context
import android.util.Log
import android.widget.Toast

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