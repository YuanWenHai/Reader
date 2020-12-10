package com.will.reader.print

import android.content.Context
import android.util.DisplayMetrics
import androidx.lifecycle.LiveData
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
class PrintViewModel(private val bookRepos: BookRepository): ViewModel() {
    private val printConfig: MutableLiveData<PrintConfig> = MutableLiveData()
    private val printerPage: MutableLiveData<Printer.PrinterPage> = MutableLiveData()
    private var printer: Printer? = null


    fun printerPage(): LiveData<Printer.PrinterPage> = printerPage

    fun initializePrinter(context: Context,book: Book,config: PrintConfig,screen: DisplayMetrics){
        if(printer == null){
            printer = Printer(book,config,screen)
        }else{
            printer!!.printWithNewConfig(context,config)
        }
        printerPage.value = printer?.print(context)
    }



    fun saveBookState(){
        printer?.let {
            viewModelScope.launch {
                bookRepos.updateBook(it.getCurrentBookStateForSave())
            }
        }
    }

    fun inCreaseTextSize(context: Context){
        printer?.let {
            val oldConfig = it.getConfig()
            val newConfig = oldConfig.increaseTextSize(context.resources.displayMetrics.density)
            viewModelScope.launch {
                PrintConfigRepos.getInstance(context).save(newConfig)
            }
        }
    }
    fun decreaseTextSize(context: Context){
        printer?.let {
            val oldConfig = it.getConfig()
            val newConfig = oldConfig.decreaseTextSize(context.resources.displayMetrics.density)
            viewModelScope.launch {
                PrintConfigRepos.getInstance(context).save(newConfig)
            }
        }

    }
    fun pageUp(context: Context){
        printer?.pageUp(context)?.let {
            printerPage.value = it
        }
    }
    fun pageDown(context: Context){
        printer?.pageDown(context)?.let {
            printerPage.value = it
        }

    }

}