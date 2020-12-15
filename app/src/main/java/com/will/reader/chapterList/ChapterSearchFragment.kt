package com.will.reader.chapterList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.will.reader.base.BaseDialogFragment
import com.will.reader.databinding.FragmentChapterSearchBinding

/**
 * created  by will on 2020/12/15 15:54
 */
class ChapterSearchFragment: BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentChapterSearchBinding.inflate(inflater,container,false).root
    }
}