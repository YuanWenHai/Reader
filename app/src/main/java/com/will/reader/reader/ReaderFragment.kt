package com.will.reader.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.will.reader.R
import com.will.reader.chapterList.ChapterListFragment
import com.will.reader.data.AppDataBase
import com.will.reader.data.ChapterRepository
import com.will.reader.databinding.FragmentReaderBinding
import com.will.reader.reader.viewmodel.PrintViewModelFactory
import com.will.reader.reader.viewmodel.ReaderViewModel
import com.will.reader.viewmodel.AppViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/11/29 17:31
 */
class ReaderFragment: Fragment() {
    private val appViewModel: AppViewModel by activityViewModels()
    private val viewModel: ReaderViewModel by viewModels{
        PrintViewModelFactory(
            appViewModel.book().value!!,
            ChapterRepository.getInstance(AppDataBase.getInstance(requireContext()).getChapterDao())
        )

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentReaderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reader,container,false)
        viewLifecycleOwner.lifecycleScope.launch{
            //监听config变化
            PrintConfigRepos.getInstance(requireContext()).get(requireContext().resources.displayMetrics.density).collectLatest {
                config ->
                binding.fragmentReaderView.setConfig(config)
                viewModel.resetPage(requireContext(),config)
                binding.fragmentReaderTextSize.text = getString(R.string.current_text_size).plus("${config.textSize}")
            }
        }
        initViewEvent(binding)
        observeData(binding)
        return binding.root
    }


    private fun observeData(binding: FragmentReaderBinding){

        viewModel.currentChapter().observe(viewLifecycleOwner){
            binding.fragmentReaderChapterText.text = it
        }
        viewModel.page().observe(viewLifecycleOwner){
            binding.fragmentReaderView.submitContent(it)
            binding.fragmentReaderProgressText.text = resources.getString(R.string.current_progress).plus(it.progress)
        }
        viewModel.currentEncode().observe(viewLifecycleOwner){
            binding.fragmentReaderEncodeText.text = it
        }
        viewModel.showMenu().observe(viewLifecycleOwner){
            binding.fragmentReaderMenu.visibility = if(it) View.VISIBLE else View.INVISIBLE
        }
    }


    private fun initViewEvent(binding: FragmentReaderBinding){

        binding.fragmentReaderView.setOnClickListener {
            which ->
            when(which){
                ReaderView.LEFT_CLICK -> viewModel.leftClick(requireContext())
                ReaderView.CENTER_CLICK ->  viewModel.changeMenuState()
                ReaderView.RIGHT_CLICK -> viewModel.rightClick(requireContext())
            }
        }
        binding.fragmentReaderTextSizeIncrease.setOnClickListener{
            viewModel.inCreaseTextSize(requireContext())
        }

        binding.fragmentReaderTextSizeDecrease.setOnClickListener{
            viewModel.decreaseTextSize(requireContext())
        }

        binding.fragmentReaderChapterButton.setOnClickListener{
            findNavController().navigate(ReaderFragmentDirections.actionReaderFragmentToChapterListFragment())
            viewModel.closeMenu()
        }
        binding.fragmentReaderEncodeButton.setOnClickListener {
            val newBook = viewModel.changeEncode(requireContext())
            appViewModel.updateBook(newBook)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            if(viewModel.canGoBack()){
                findNavController().popBackStack()
            }
        }
        binding.fragmentReaderProgressButton.setOnClickListener {
            SkipProgressFragment().show(parentFragmentManager,"skip_progress_fragment")
        }
        setFragmentResultListener(SkipProgressFragment.REQUEST_KEY){
            _, bundle ->
            val value = bundle.getFloat(SkipProgressFragment.VALUE_KEY)
            viewModel.skipToProgress(requireContext(),value)
            viewModel.closeMenu()
        }
        setFragmentResultListener(ChapterListFragment.REQUEST_KEY){
            _,bundle ->
            val value = bundle.getInt(ChapterListFragment.VALUE_KEY)
            viewModel.skipToPosition(requireContext(),value)
        }
    }


    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
    override fun onPause() {
        super.onPause()
        val book = viewModel.getBookForSave()
        appViewModel.updateBook(book)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}