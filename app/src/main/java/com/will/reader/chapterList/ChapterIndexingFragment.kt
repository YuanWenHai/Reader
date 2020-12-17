package com.will.reader.chapterList

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.will.reader.R
import com.will.reader.base.BaseDialogFragment
import com.will.reader.chapterList.viewmodel.ChapterIndexingViewModel
import com.will.reader.data.AppDataBase
import com.will.reader.data.ChapterRepository
import com.will.reader.data.model.Book
import com.will.reader.databinding.FragmentChapterIndexingBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * created  by will on 2020/12/12 18:21
 */
class ChapterIndexingFragment private constructor(): BaseDialogFragment() {

    private val viewModel: ChapterIndexingViewModel by viewModels{
        object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ChapterIndexingViewModel(ChapterRepository.getInstance(AppDataBase.getInstance(requireContext()).getChapterDao())) as T
            }
        }
    }
    private lateinit var finder: ChapterFinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finder = ChapterFinder(getBook(this))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       //isCancelable = false
       val binding = FragmentChapterIndexingBinding.inflate(layoutInflater,container,false)
       initView(binding)
       return binding.root
    }

    private fun initView(binding: FragmentChapterIndexingBinding){
        binding.fragmentChapterIndexProgress.max = 100
        binding.fragmentChapterIndexProgressText.text = "0%"
        binding.fragmentChapterIndexChapterText.text = "未发现章节"

        binding.fragmentChapterIndexSearchButton.setOnClickListener {
            binding.fragmentChapterIndexSearchLayout.visibility = View.INVISIBLE
            binding.fragmentChapterIndexProgressLayout.visibility = View.VISIBLE
            val checkedRadioId = binding.fragmentChapterIndexRadioGroup.checkedRadioButtonId
            val keyword = binding.root.findViewById<RadioButton>(checkedRadioId).text.toString()
            isCancelable = false
            viewLifecycleOwner.lifecycleScope.launch{
                finder.indexing(keyword).collect {
                    when(it){
                        is ChapterFinder.FindResult.Find -> {
                            binding.fragmentChapterIndexChapterText.text = it.chapter.name
                            viewModel.addChapter(it.chapter)
                        }
                        is ChapterFinder.FindResult.Progress -> {
                            binding.fragmentChapterIndexProgress.progress = it.progress
                            binding.fragmentChapterIndexProgressText.text = "${it.progress}%"
                        }
                        is ChapterFinder.FindResult.Finish -> {
                            binding.fragmentChapterIndexProgressText.text = "检索完毕"
                            binding.fragmentChapterIndexProgress.progress = 100
                            viewModel.commit()
                            dismiss()
                        }
                    }

                }
            }
        }
        binding.fragmentChapterIndexCancelButton.setOnClickListener {
            dismiss()
        }
    }







    companion object{
        private const val DATA_BOOK = "data_book"
        fun get(book: Book): ChapterIndexingFragment{
            return ChapterIndexingFragment().also {
                val bundle = Bundle()
                bundle.putSerializable(DATA_BOOK,book)
                it.arguments = bundle
            }
        }
        private fun getBook(fragment: ChapterIndexingFragment): Book{
            fragment.arguments?.let {
                return it.getSerializable(DATA_BOOK) as Book
            } ?: throw IllegalArgumentException("must pass book to here")
        }

    }

}