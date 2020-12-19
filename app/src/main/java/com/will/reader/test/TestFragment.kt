package com.will.reader.test

import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.will.reader.R
import com.will.reader.base.BaseFragment
import com.will.reader.chapterList.ChapterListAdapter
import com.will.reader.chapterList.view.IndexRecyclerView
import java.util.jar.Attributes

/**
 * created  by will on 2020/12/17 11:25
 */
class TestFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test,container,false)
        val recyclerView = view.findViewById<IndexRecyclerView>(R.id.fragment_test_recycler)
        return view
    }
}