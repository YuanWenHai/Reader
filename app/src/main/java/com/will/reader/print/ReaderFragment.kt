package com.will.reader.print

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.will.reader.R
import com.will.reader.data.AppDataBase
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.databinding.FragmentReaderBinding
import com.will.reader.util.LOG_TAG
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/11/29 17:31
 */
class ReaderFragment: Fragment() {
    private val viewModel: ReaderViewModel by viewModels{
        PrintViewModelFactory(
            arg.book,
            BookRepository.getInstance(AppDataBase.getInstance(requireContext()).getBookDao()),
            ChapterRepository.getInstance(AppDataBase.getInstance(requireContext()).getChapterDao())
        )

    }
    private val arg: ReaderFragmentArgs by navArgs()

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
        initViewClickEvent(binding)
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

        }
    }


    private fun initViewClickEvent(binding: FragmentReaderBinding){

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

        binding.fragmentReaderChapterButton.setOnClickListener{
            findNavController().navigate(ReaderFragmentDirections.actionReaderFragmentToChapterListFragment(viewModel.getBook()))
        }
        binding.fragmentReaderEncodeButton.setOnClickListener {
            viewModel.changeEncode(requireContext())
        }
        binding.fragmentReaderProgressButton.setOnClickListener {
            showChangeProgressDialog()
        }
    }
    private fun changeMenuState(menu: View){
        menuClickFlag = !menuClickFlag
        menu.visibility = if(menuClickFlag) View.VISIBLE else View.GONE
    }

    // TODO: 2020/12/19  alert dialog的window会导致app window中设置的systemUiVisibility失效
    private fun showChangeProgressDialog(){
        val view = EditText(requireContext())
        view.inputType = EditorInfo.TYPE_NUMBER_FLAG_DECIMAL or EditorInfo.TYPE_CLASS_NUMBER
        view.hint = viewModel.page().value?.progress ?: "0%"
        val dialog = AlertDialog.Builder(requireContext()).setView(view)
            .setTitle("输入进度")
            .create()
        dialog.show()
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