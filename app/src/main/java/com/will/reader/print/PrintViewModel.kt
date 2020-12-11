package com.will.reader.print

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.util.getFormattedTime
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * created  by will on 2020/12/3 12:51
 */
class PrintViewModel(private val printer: Printer,private val bookRepos: BookRepository,private val chapterRepository: ChapterRepository): ViewModel() {
    private val page: MutableLiveData<ReaderView.Content> = MutableLiveData()
    private val currentChapter: MutableLiveData<String> = MutableLiveData()
    private var config: PrintConfig? = null
    private var screenWidth = 0f
    private var screenHeight = 0f

    fun page(): LiveData<ReaderView.Content> = page
    fun currentChapter(): LiveData<String> = currentChapter

    fun resetPage(context: Context, config: PrintConfig){
       screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
       screenHeight = context.resources.displayMetrics.heightPixels.toFloat()
       this.config = config
       applyConfigChanges(config,context)
    }

    fun saveBookState(){
        viewModelScope.launch {
            bookRepos.updateBook(printer.getCurrentBookStateForSave())
        }
    }

    fun inCreaseTextSize(context: Context){
        config?.let {
            val newConfig = it.increaseTextSize(context.resources.displayMetrics.density)
            viewModelScope.launch {
                PrintConfigRepos.getInstance(context).save(newConfig)
            }
        }
    }
    fun decreaseTextSize(context: Context){
        config?.let {
            val newConfig = it.decreaseTextSize(context.resources.displayMetrics.density)
            viewModelScope.launch {
                PrintConfigRepos.getInstance(context).save(newConfig)
            }
        }
    }

    private fun applyConfigChanges(config: PrintConfig,context: Context){
        val c = Printer.Config.build(config,screenWidth,screenHeight)
        val printed = printer.print(c)
        val content = generateReaderContent(printed,context)
        updateCurrentChapter(printed.currentPosition)
        page.value = content
    }

    fun pageUp(context: Context){
        config?.let {
            val printed = printer.pageUp(Printer.Config.build(it,screenWidth,screenHeight))
            updateCurrentChapter(printed.currentPosition)
            page.value = generateReaderContent(printed,context)
        }

    }
    fun pageDown(context: Context){
        config?.let {
            val config = Printer.Config.build(it,screenWidth,screenHeight)
            val printed = printer.pageDown(config)
            val content = generateReaderContent(printed,context)
            updateCurrentChapter(printed.currentPosition)
            page.value = content
        }
    }

    private fun generateReaderContent(page: Printer.Page,context: Context): ReaderView.Content{
        val timeText = "\uD83D\uDD52${getFormattedTime("HH:mm")}"
        val progress = page.currentPosition*100/printer.getBook().size.toFloat()
        val progressText = "%.3f".format(progress).plus("%")
        val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context.registerReceiver(null,it)
        }
        val batteryLevelText = batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE,-1)
            val percentage = level*100/scale.toFloat()
            "\uD83D\uDD0B${percentage.roundToInt()}%"
        } ?: ""
        return ReaderView.Content(page.lines,timeText,progressText,batteryLevelText)
    }


    private fun updateCurrentChapter(currentPosition: Int){
        viewModelScope.launch {
            val chapterName = chapterRepository.getChapterByBookAndPositionRange(printer.getBook(),currentPosition)?.name ?: "尚未获取章节内容"
            currentChapter.postValue(chapterName)
        }
    }

    override fun onCleared() {
        super.onCleared()
        printer.closeBook()
    }
}