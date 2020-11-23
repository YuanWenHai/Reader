package com.will.reader.scan

import androidx.lifecycle.MutableLiveData
import com.will.reader.bookList.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class FileScanner {



    companion object{
        private const val CURSOR_FINISHED = "search finished"

        fun scan(lifecycleScope: CoroutineScope,dir: File,filter: (file: File) -> Boolean = { defaultFilter(dir)},
                 list: MutableLiveData<MutableList<FileItem>>,
                 cursor: MutableLiveData<String>) {

            if(!dir.exists()){
                return
            }
            lifecycleScope.launch(IO){
                rec(dir,filter,list,cursor)
                cursor.postValue(CURSOR_FINISHED)
            }
        }
        private fun rec(dir: File, filter: (file: File) -> Boolean,
                        list: MutableLiveData<MutableList<FileItem>>,
                        cursor: MutableLiveData<String>){
            if(!dir.exists()){
                return
            }
            //skip hidden directory
            if (dir.isDirectory && !dir.isHidden){
                cursor.postValue(dir.path)
                dir.listFiles()?.forEach {
                    rec(it,filter,list,cursor)
                }
            }else if (dir.isFile){
                if(filter(dir)) {
                    list.value?.let {
                        val fileItem = FileItem(name = dir.name,size = Util.byteSizeToFormattedString(dir.length()),path = dir.path,false)
                        it.add(fileItem)
                        list.postValue(it)
                    }
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
    }



}