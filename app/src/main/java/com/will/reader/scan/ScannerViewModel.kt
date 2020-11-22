package com.will.reader.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * created  by will on 2020/11/22 17:34
 */
class ScannerViewModel: ViewModel() {

    private val list: MutableLiveData<MutableList<FileItem>> = MutableLiveData()

    fun getList(): LiveData<MutableList<FileItem>> = list


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