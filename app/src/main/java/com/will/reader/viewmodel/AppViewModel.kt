package com.will.reader.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.squareup.moshi.internal.Util
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book
import com.will.reader.extensions.getRealName
import com.will.reader.extensions.isBook
import com.will.reader.util.LOG_TAG
import com.will.reader.util.copy
import com.will.reader.util.makeLongToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths

/**
 * created  by will on 2020/12/20 11:32
 */
class AppViewModel(
    private val bookRepos: BookRepository,
    private val chapterRepos: ChapterRepository
) : ViewModel() {
    private val currentBook: MutableLiveData<Book> = MutableLiveData()
    fun book(): LiveData<Book> = currentBook


    fun updateBook(book: Book) {
        viewModelScope.launch {
            currentBook.value = book
            bookRepos.updateBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            bookRepos.deleteBook(book)
            chapterRepos.deleteChapterByBook(book)
        }
    }

    /**
     * 检查该文件是否存在,有修改编辑则更新数据库
     */
    fun checkIfBookExists(book: Book): Boolean {
        val bookFile = File(book.path)
        if (bookFile.isFile) {
            if (book.size != bookFile.length()) {
                viewModelScope.launch {
                    bookRepos.updateBook(book)
                }
            }
        }
        return bookFile.isFile
    }

    fun addBook(file: File) {
        if (!file.exists()) {
            Log.w(LOG_TAG, "file: ${file.path} does not exits,save canceled")
            return
        }
        if (!file.isBook()) {
            Log.w(LOG_TAG, "file: ${file.path} is not book,save canceled")
            return
        }
        viewModelScope.launch {
            bookRepos.saveBook(Book.build(file))
        }
    }

    fun addBook(uri: Uri?, context: Context) {
        if (uri != null) {
            viewModelScope.launch {
                try {
                    val fileName = uri.getRealName(context) ?: "${System.currentTimeMillis()}.txt"
                    val targetFile = File(context.getExternalFilesDir("books"), fileName)
                    copy(context.contentResolver.openInputStream(uri), targetFile)
                    if (!targetFile.exists()) {
                        makeLongToast(context, "添加失败，文件 ${targetFile.path}不存在")
                        return@launch
                    }
                    if (!targetFile.isBook()) {
                        makeLongToast(context, "添加失败，文件 ${targetFile.path}不是文本文件")
                        return@launch
                    }
                    addBook(targetFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            makeLongToast(context, "添加失败，文件读取错误")
        }
    }

}