package com.will.reader.print

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
            BookRepository.getInstance(AppDataBase.getInstance(requireContext()).getBookDao()))
    }
    private val arg: ReaderFragmentArgs by navArgs()
    private var menuClickFlag = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentReaderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reader,container,false)
        viewLifecycleOwner.lifecycleScope.launch{
            //监听config变化
            PrintConfigRepos.getInstance(requireContext()).get(requireContext().resources.displayMetrics.density).collectLatest {
                config ->
                binding.fragmentReaderView.setConfig(config)
                viewModel.initializePrinter(requireContext(),arg.book.copy(encode = "gbk"),config, resources.displayMetrics)
                initViewCurrentValue(binding,config)
            }
        }
        initView(binding)
        return binding.root
    }


    private fun initViewCurrentValue(binding: FragmentReaderBinding, config: PrintConfig){
        binding.fragmentReaderTextSize.text = getString(R.string.current_text_size).plus("${config.textSize}")
    }

    private fun initView(binding: FragmentReaderBinding){
        viewModel.printerPage().observe(viewLifecycleOwner){
            binding.fragmentReaderView.submitPage(it)
        }

        binding.fragmentReaderView.setOnClickListener {
            which ->
            when(which){
                ReaderView.LEFT_CLICK -> {
                    if(menuClickFlag){
                        changeMenuState(binding.fragmentReaderMenu)
                        return@setOnClickListener
                    }
                    viewModel.pageUp(requireContext())

                }
                ReaderView.CENTER_CLICK ->  changeMenuState(binding.fragmentReaderMenu)
                ReaderView.RIGHT_CLICK -> {
                    if(menuClickFlag){
                        changeMenuState(binding.fragmentReaderMenu)
                        return@setOnClickListener
                    }
                    viewModel.pageDown(requireContext())
                }
            }
        }
        binding.fragmentReaderTextSizeIncrease.setOnClickListener{
            viewModel.inCreaseTextSize(requireContext())
        }

        binding.fragmentReaderTextSizeDecrease.setOnClickListener{
            viewModel.decreaseTextSize(requireContext())
        }
    }
    private fun changeMenuState(menu: View){
        menuClickFlag = !menuClickFlag
        menu.visibility = if(menuClickFlag) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
    override fun onPause() {
        super.onPause()
        viewModel.saveBookState()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

    }

}