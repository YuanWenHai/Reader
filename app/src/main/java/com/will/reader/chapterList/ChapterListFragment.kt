package com.will.reader.chapterList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.will.reader.chapterList.viewmodel.ChapterListViewModel
import com.will.reader.chapterList.viewmodel.ChapterListViewModelFactory
import com.will.reader.data.AppDataBase
import com.will.reader.data.ChapterRepository
import com.will.reader.databinding.FragmentChapterListBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * created  by will on 2020/12/11 15:53
 */
class ChapterListFragment: Fragment() {
    private val viewModel: ChapterListViewModel by viewModels{
        ChapterListViewModelFactory(
            args.book,
            ChapterRepository.getInstance(AppDataBase.getInstance(requireContext()).getChapterDao())
        )
    }

    private val args: ChapterListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChapterListBinding.inflate(inflater,container,false)
        initView(binding)
        return binding.root
    }

    private fun initView(binding: FragmentChapterListBinding){
        val adapter = ChapterListAdapter()
        binding.fragmentChapterListToolbar.title = args.book.name
        binding.fragmentChapterListRecycler.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.chapterFlow.collectLatest {
                adapter.submitData(it)
            }
            adapter.loadStateFlow.collectLatest {
                binding.fragmentChapterListRecycler.visibility = if(adapter.itemCount == 0) View.INVISIBLE else View.VISIBLE
            }
        }
        val parent = requireActivity() as AppCompatActivity
        parent.setSupportActionBar(binding.fragmentChapterListToolbar)
        parent.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fragmentChapterListToolbar.setNavigationOnClickListener{parent.onBackPressed()}

    }
}