package com.will.reader.print

import android.content.Context
import android.util.DisplayMetrics
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.will.reader.data.BookDao
import com.will.reader.data.BookRepository
import com.will.reader.data.model.Book
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/12/3 12:51
 */
class PrintViewModel(private val printer: Printer,private val bookRepos: BookRepository): ViewModel() {
    private val printConfig: MutableLiveData<PrintConfig> = MutableLiveData()


    fun getPrinter(): Printer{
        return printer
    }

    fun saveBookState(){
        viewModelScope.launch {
            bookRepos.updateBook(printer.getCurrentBookStateForSave())
        }
    }

    fun inCreaseTextSize(context: Context){
        val oldConfig = printer.getConfig()
        val newConfig = oldConfig.increaseTextSize(context.resources.displayMetrics.density)
        viewModelScope.launch {
            PrintConfigRepos.getInstance(context).save(newConfig)
        }
    }
    fun decreaseTextSize(context: Context){
        val oldConfig = printer.getConfig()
        val newConfig = oldConfig.decreaseTextSize(context.resources.displayMetrics.density)
        viewModelScope.launch {
            PrintConfigRepos.getInstance(context).save(newConfig)
        }
    }

}