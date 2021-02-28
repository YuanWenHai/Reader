package com.will.reader.scan

import com.will.reader.extensions.isBook
import com.will.reader.extensions.toFormattedSize
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque

class FileScanner {

    private val resultList = mutableListOf<FileItem>()

    suspend fun scan(dir: File,
             itemCallback: (fileItem: FileItem) -> Unit,
             cursorCallback: (cursor: String) -> Unit) {

        if(!dir.exists()){
            return
        }
        withContext(IO){
            rec(dir,{file -> defaultFilter(file)},itemCallback,cursorCallback)
            cursorCallback(CURSOR_FINISHED)
        }
    }

    fun scan(dir: File): Flow<FileItem> = flow {
        val s = Stack<File>()
        s.push(dir)
        while (s.size > 0){
            val d = s.pop()
            val files = d.listFiles()
            files?.forEach {
                f ->
                if(f.isFile){
                    if(defaultFilter(f)){
                        emit(FileItem(f))
                    }
                }else if(f.isDirectory){
                    s.push(f)
                }
            }
        }
    }.flowOn(IO)


    private fun rec(f: File, filter: (file: File) -> Boolean,
                    itemCallback: (fileItem: FileItem) -> Unit,
                    cursorCallback: (cursor: String) -> Unit){
        if(!f.exists() || f.isHidden){
            return
        }
        if (f.isDirectory){
            cursorCallback(f.path)
            f.listFiles()?.forEach {
                rec(it,filter,itemCallback,cursorCallback)
            }
        }else if (f.isFile){
            if(filter(f)) {
                val fileItem = FileItem(f)
                itemCallback(fileItem)
            }

        }
    }
    private fun defaultFilter(file: File): Boolean{
        var flag = false
        val lengthLimit = 1024 * 100 // 50kb

        if(file.length() >= lengthLimit && file.isBook()){
            flag = true
        }
        return flag
    }

    companion object{
        const val CURSOR_FINISHED = "search finished"

    }



}
