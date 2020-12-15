package com.will.reader.chapterList

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.will.reader.R
import com.will.reader.base.BaseFragment
import com.will.reader.chapterList.viewmodel.ChapterListViewModel
import com.will.reader.chapterList.viewmodel.ChapterListViewModelFactory
import com.will.reader.data.AppDataBase
import com.will.reader.data.ChapterRepository
import com.will.reader.databinding.FragmentChapterListBinding
import com.will.reader.util.LOG_TAG
import com.will.reader.util.makeLongToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/12/11 15:53
 */
class ChapterListFragment: BaseFragment() {
    private val viewModel: ChapterListViewModel by viewModels{
        ChapterListViewModelFactory(
            args.book,
            ChapterRepository.getInstance(AppDataBase.getInstance(requireContext()).getChapterDao())
        )
    }
    private val args: ChapterListFragmentArgs by navArgs()
    private var keyword = "章"

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
        setHasOptionsMenu(true)
        val parent = requireActivity() as AppCompatActivity
        parent.setSupportActionBar(binding.fragmentChapterListToolbar)
        parent.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fragmentChapterListToolbar.setNavigationOnClickListener{parent.onBackPressed()}


        val adapter = ChapterListAdapter()
        binding.fragmentChapterListToolbar.title = args.book.name
        binding.fragmentChapterListRecycler.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chapterFlow.collectLatest {
                adapter.submitData(it)
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                if(it.refresh is LoadState.NotLoading){
                    binding.fragmentChapterListRecycler.visibility = if(adapter.itemCount == 0) View.INVISIBLE else View.VISIBLE
                }
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chapter_list,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_chapter_add){
            ChapterIndexingFragment.get(args.book).show(parentFragmentManager,"progress_bar")
        }else if(item.itemId == R.id.menu_chapter_delete){
            viewModel.deleteAllChapter()
            makeLongToast(requireContext(),"已删除章节信息")
        }
        return super.onOptionsItemSelected(item)
    }
}