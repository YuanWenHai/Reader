package com.will.reader.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File

/**
 * created  by will on 2020/11/22 17:34
 */
class ScannerViewModel: ViewModel() {
    private val rootDir = File("sdcard/")
    private val list = MutableLiveData<MutableList<FileItem>>()
    private val currentCursor = MutableLiveData<String>()
    fun getList(): LiveData<MutableList<FileItem>> = list
    fun getCursor(): LiveData<String> = currentCursor

    fun scan(){
        FileScanner.scan(viewModelScope,rootDir,list = list,cursor = currentCursor)
    }

    fun addFileItem(item: FileItem){
        list.value?.let {
            it.add(item)
            list.value = it
        }
    }
    fun addFileItem(items: List<FileItem>){
        list.value?.let {
            it.addAll(items)
            list.value = it
        }
    }
    fun changeSelectState(index: Int){
        list.value?.let {
            val old = it[index]
            it[index] = old.copy(selected = !old.selected)
            list.value = it
        }
    }
}