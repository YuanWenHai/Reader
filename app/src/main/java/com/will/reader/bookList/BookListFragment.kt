package com.will.reader.bookList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.will.reader.bookList.viewmodel.BookListViewModel
import com.will.reader.bookList.viewmodel.BookViewModelFactory
import com.will.reader.data.AppDataBase
import com.will.reader.databinding.FragmentBookListBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * created  by will on 2020/11/22 12:15
 */
class BookListFragment: Fragment() {
    private val viewModel: BookListViewModel by viewModels{
        BookViewModelFactory(AppDataBase.getInstance(requireContext()).getBookDao())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBookListBinding.inflate(inflater,container,false)
        init()
        return binding.root
    }
    private fun init(){
        val adapter = BookListAdapter()
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.bookFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }
}