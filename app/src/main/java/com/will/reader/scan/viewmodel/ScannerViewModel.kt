package com.will.reader.scan.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.will.reader.data.BookRepository
import com.will.reader.data.model.Book
import com.will.reader.extensions.isBook
import com.will.reader.scan.FileItem
import com.will.reader.scan.FileScanner
import com.will.reader.scan.FileScanner.Companion.CURSOR_FINISHED
import com.will.reader.util.LOG_TAG
import com.will.reader.util.outputToFile
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

/**
 * created  by will on 2020/11/22 17:34
 */
class ScannerViewModel(private val scanner: FileScanner, private val bookRepos: BookRepository): ViewModel() {
    private val rootDir = Environment.getExternalStorageDirectory()

    private val list = MutableLiveData<MutableList<FileItem>>()
    private val currentCursor = MutableLiveData<String>()
    private val hasPermission = MutableLiveData<Boolean>()
    fun getList(): LiveData<MutableList<FileItem>> = list
    fun getCursor(): LiveData<String> = currentCursor
    fun hasPermission(): LiveData<Boolean> = hasPermission


    fun scan(){
        viewModelScope.launch {
            scanner.scan(rootDir).collect {
                val prev = list.value ?: mutableListOf()
                prev.add(it)
                list.value = prev
            }
            currentCursor.value = CURSOR_FINISHED
        }
    }


    fun setHasPermission(which: Boolean){
        hasPermission.value = which
    }
    fun copyUriContentToAppStorage(uri: Uri,context: Context){
        val input = context.contentResolver.openInputStream(uri)
        val dest = context.openFileOutput("",Context.MODE_PRIVATE)
    }

    fun changeSelectState(index: Int){
        val value = list.value!!
        val oldItem = value[index]
        value[index] = oldItem.copy(selected = !oldItem.selected)
        list.value = value
    }
    fun save(file: File){
        if(!file.exists()){
            Log.w(LOG_TAG,"file: ${file.path} does not exits,save canceled")
            return
        }
        if(!file.isBook()){
            Log.w(LOG_TAG,"file: ${file.path} is not book,save canceled")
            return
        }
        viewModelScope.launch {
            bookRepos.saveBook(Book.build(file))
        }
    }
    private fun save(fileItems: List<FileItem>){
        viewModelScope.launch {
            val list = mutableListOf<Book>()
            fileItems.forEach{
                val file = File(it.path)
                if(file.exists() && file.isBook()){
                    list.add(Book.build(file))
                }else{
                    Log.w(LOG_TAG,"ignore file: ${file.path},cause it does not exist or is not book")
                }
            }
            bookRepos.saveBook(list)
        }
    }
    fun saveSelectedFile(){
        val selected = list.value?.filter { it.selected }
        selected?.let {
            save(it)
        }
    }
}