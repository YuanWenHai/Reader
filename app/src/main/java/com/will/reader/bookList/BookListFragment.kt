package com.will.reader.bookList

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.will.reader.R
import com.will.reader.base.BaseFragment
import com.will.reader.bookList.viewmodel.BookListViewModel
import com.will.reader.bookList.viewmodel.BookViewModelFactory
import com.will.reader.data.AppDataBase
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.databinding.FragmentBookListBinding
import com.will.reader.util.makeLongToast
import com.will.reader.viewmodel.AppViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * created  by will on 2020/11/22 12:15
 */
class BookListFragment: BaseFragment() {
    private val appViewModel: AppViewModel by activityViewModels()
    private val viewModel: BookListViewModel by viewModels{
        val appDb =  AppDataBase.getInstance(requireContext())
        BookViewModelFactory(
            BookRepository.getInstance(appDb.getBookDao()),
            ChapterRepository.getInstance(appDb.getChapterDao())
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBookListBinding.inflate(inflater,container,false)
        init(binding)
        return binding.root
    }
    private fun init(binding: FragmentBookListBinding){
        setHasOptionsMenu(true)
        val parent = activity as AppCompatActivity
        parent.setSupportActionBar(binding.bookListToolbar)
        val adapter = BookListAdapter{
            book ->
            runWithStoragePermission({
                if(viewModel.checkBook(book)){
                    appViewModel.updateBook(book)
                    findNavController().navigate(BookListFragmentDirections.actionBookListFragmentToReaderFragment())
                }else{
                    Toast.makeText(requireContext(),"该书籍已不存在！",Toast.LENGTH_LONG).show()
                    viewModel.deleteBook(book)
                }
            },{
                makeLongToast(requireContext(),"未授予存储权限，无法打开书籍")
            })
        }
        val swipeHandler = object: SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deleted = (viewHolder as BookListAdapter.ViewHolder).book
                deleted?.let { appViewModel.deleteBook(deleted) }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.bookListRecycler)
        binding.bookListRecycler.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.bookFlow.collectLatest {
                adapter.submitData(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                if(it.refresh is LoadState.NotLoading){
                    binding.bookListEmptyView.visibility = if(adapter.itemCount == 0) View.VISIBLE else View.GONE
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.book_list,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_book_list_add){
            findNavController().navigate(BookListFragmentDirections.actionBookListFragmentToScannerFragment())
        }
        return super.onOptionsItemSelected(item)
    }
}