package com.will.reader.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * created  by will on 2020/11/26 16:42
 */
class ScannerViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ScannerViewModel(FileScanner()) as T
    }
}