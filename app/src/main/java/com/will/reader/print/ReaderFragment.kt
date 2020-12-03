package com.will.reader.print

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.will.reader.data.model.Book

/**
 * created  by will on 2020/11/29 17:31
 */
class ReaderFragment: Fragment() {
    val arg: ReaderFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val book = arg.book.copy(encode = "gbk")
        val config = PrintConfig.default(requireContext())
        val printer = Printer(book,config,resources.displayMetrics)
        val view = ReaderView(requireContext(),printer)
        return view
    }
}