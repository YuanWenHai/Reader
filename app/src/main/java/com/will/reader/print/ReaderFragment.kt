package com.will.reader.print

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.will.reader.R
import com.will.reader.data.AppDataBase
import com.will.reader.data.BookRepository
import com.will.reader.databinding.FragmentReaderBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/11/29 17:31
 */
class ReaderFragment: Fragment() {
    private val viewModel: PrintViewModel by viewModels{
        PrintViewModelFactory(
            arg.book.copy(encode = "gbk"),
            resources.displayMetrics,
            BookRepository.getInstance(AppDataBase.getInstance(requireContext()).getBookDao()))
    }

    private val arg: ReaderFragmentArgs by navArgs()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentReaderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reader,container,false)
        val printer = viewModel.getPrinter()
        binding.fragmentReaderView.setPrinter(printer)
        viewLifecycleOwner.lifecycleScope.launch{
            PrintConfigRepos.getInstance(requireContext()).get(requireContext().resources.displayMetrics.density).collectLatest {
                viewModel.getPrinter().setConfig(it)
                initViewCurrentValue(binding,it)
            }
        }
        initView(binding)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveBookState()
    }

    private fun initViewCurrentValue(binding: FragmentReaderBinding, config: PrintConfig){
        binding.fragmentReaderTextSize.text = getString(R.string.current_text_size).plus("${config.textSize}")
        binding.fragmentReaderView.refresh()
    }

    private fun initView(binding: FragmentReaderBinding){
        binding.fragmentReaderMenu.visibility = View.GONE
        binding.fragmentReaderTextSizeIncrease.setOnClickListener{
            viewModel.inCreaseTextSize(requireContext())
            binding.fragmentReaderView.refresh()
        }
        binding.fragmentReaderTextSizeDecrease.setOnClickListener{
            viewModel.decreaseTextSize(requireContext())
            binding.fragmentReaderView.refresh()
        }
    }

}