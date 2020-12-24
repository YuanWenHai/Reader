package com.will.reader.scan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.will.reader.data.BookRepository
import com.will.reader.scan.FileScanner

/**
 * created  by will on 2020/11/26 16:42
 */
class ScannerViewModelFactory(private val bookRepos: BookRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ScannerViewModel(FileScanner(),bookRepos) as T
    }
}