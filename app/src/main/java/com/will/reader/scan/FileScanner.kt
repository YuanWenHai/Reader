package com.will.reader.scan

import androidx.lifecycle.MutableLiveData
import com.will.reader.bookList.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
class FileScanner {

    private val resultList = mutableListOf<FileItem>()

    fun scan(lifecycleScope: CoroutineScope,dir: File,
             itemCallback: (fileItem: FileItem) -> Unit,
             cursorCallback: (cursor: String) -> Unit) {

        if(!dir.exists()){
            return
        }
        lifecycleScope.launch(IO){
            rec(dir,{file -> defaultFilter(file)},itemCallback,cursorCallback)
            cursorCallback(CURSOR_FINISHED)
        }
    }
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
                val fileItem = FileItem(name = f.name,size = Util.byteSizeToFormattedString(f.length()),path = f.path,false)
                itemCallback(fileItem)
            }

        }
    }
    private fun defaultFilter(file: File): Boolean{
        var flag = false
        val lengthLimit = 1024 * 100 // 50kb

        if(file.length() >= lengthLimit){
            val suffix = with(file.name){
                substring(lastIndexOf(".") + 1, length).toUpperCase(Locale.ROOT)
            }
            if(suffix == "TXT" || suffix == "TEXT"){
                flag = true
            }
        }
        return flag
    }

    companion object{
        const val CURSOR_FINISHED = "search finished"

    }



}