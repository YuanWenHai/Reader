package com.will.reader.scan

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * created  by will on 2020/11/22 17:34
 */
class ScannerViewModel(private val scanner: FileScanner): ViewModel() {
    private val rootDir = Environment.getExternalStorageDirectory()

    private val list = MutableLiveData<MutableList<FileItem>>()
    private val currentCursor = MutableLiveData<String>()
    private val hasPermission = MutableLiveData<Boolean>()
    fun getList(): LiveData<MutableList<FileItem>> = list
    fun getCursor(): LiveData<String> = currentCursor
    fun hasPermission(): LiveData<Boolean> = hasPermission

    fun scan(){
        scanner.scan(viewModelScope,rootDir,{fileItem -> addFileItem(fileItem)},{cursor -> currentCursor.postValue(cursor)})
    }
    fun setHasPermission(which: Boolean){
        hasPermission.value = which
    }

    fun addFileItem(item: FileItem){
        val newList = list.value?.toMutableList() ?: mutableListOf()
        newList.add(item)
        list.postValue(newList)
        /*list.value?.let {
            oldList ->
            viewModelScope.launch(Main){
                val newList = oldList.toMutableList()
                newList.add(item)
                list.value = newList
            }
        }*/
    }
    fun changeSelectState(index: Int){
        list.value?.let {
            oldList ->
            val newList = oldList.toMutableList()
            val oldItem = oldList[index]
            newList[index] = oldItem.copy(selected = !oldItem.selected)

            list.value = newList
        }
    }
}