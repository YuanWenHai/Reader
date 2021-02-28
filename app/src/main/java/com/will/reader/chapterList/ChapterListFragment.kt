package com.will.reader.chapterList

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.will.reader.R
import com.will.reader.base.BaseFragment
import com.will.reader.chapterList.viewmodel.ChapterListViewModel
import com.will.reader.chapterList.viewmodel.ChapterListViewModelFactory
import com.will.reader.data.AppDataBase
import com.will.reader.data.ChapterRepository
import com.will.reader.databinding.FragmentChapterListBinding
import com.will.reader.util.makeLongToast
import com.will.reader.viewmodel.AppViewModel

/**
 * created  by will on 2020/12/11 15:53
 */
class ChapterListFragment: BaseFragment() {
    private val appViewModel: AppViewModel by activityViewModels()
    private val viewModel: ChapterListViewModel by viewModels{
        ChapterListViewModelFactory(
            appViewModel.book().value!!,
            ChapterRepository.getInstance(AppDataBase.getInstance(requireContext()).getChapterDao())
        )
    }

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
        viewModel.start()


        // TODO: 2020/12/17  自定义正则匹配将在之后的版本迭代中完成
        binding.fragmentChapterListProfessionalText.setOnClickListener{
            makeLongToast(requireContext(),"正在开发中..")
        }
        setHasOptionsMenu(true)
        val parent = requireActivity() as AppCompatActivity
        parent.setSupportActionBar(binding.fragmentChapterListToolbar)
        parent.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fragmentChapterListToolbar.setNavigationOnClickListener{parent.onBackPressed()}


        val adapter = ChapterListAdapter{
            val bundle = Bundle()
            bundle.putInt(VALUE_KEY,it.positionInByte)
            setFragmentResult(REQUEST_KEY,bundle)
            findNavController().popBackStack()
        }
        binding.fragmentChapterListToolbar.title = appViewModel.book().value!!.name
        binding.fragmentChapterListRecycler.recycler().adapter = adapter
        binding.fragmentChapterListRecycler.recycler().addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))



        //observe data
        viewModel.getCurrentIndex().observe(viewLifecycleOwner){
            currentIndex ->
            binding.root.post {
                binding.fragmentChapterListRecycler.recycler().scrollToPosition(currentIndex)
            }

        }
        viewModel.getAllChapters().observe(viewLifecycleOwner){
            adapter.submitList(it)
            binding.fragmentChapterListRecycler.visibility = if(it.isNotEmpty()) View.VISIBLE else  View.INVISIBLE
        }

    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chapter_list,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_chapter_add){
            ChapterIndexingFragment().show(parentFragmentManager,"progress_bar")
        }else if(item.itemId == R.id.menu_chapter_delete){
            viewModel.deleteAllChapter()
            makeLongToast(requireContext(),"已删除章节信息")
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val REQUEST_KEY = "chapter_list_fragment_request_key"
        const val VALUE_KEY = "chapter_list_fragment_value_key"
    }
}