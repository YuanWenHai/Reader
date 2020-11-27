package com.will.reader.bookList

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import com.will.reader.R
import com.will.reader.bookList.viewmodel.BookListViewModel
import com.will.reader.bookList.viewmodel.BookViewModelFactory
import com.will.reader.data.AppDataBase
import com.will.reader.databinding.FragmentBookListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        init(binding)
        return binding.root
    }
    private fun init(binding: FragmentBookListBinding){
        setHasOptionsMenu(true)
        val parent = activity as AppCompatActivity
        parent.setSupportActionBar(binding.bookListToolbar)
        val adapter = BookListAdapter()
        binding.bookListRecycler.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.bookFlow.collectLatest {
                adapter.submitData(it)
            }
        }
        //binding.bookListEmptyView.visibility = View.GONE//if (adapter.itemCount == 0) View.VISIBLE else View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                // TODO: 2020/11/27 why ?
                binding.bookListEmptyView.visibility = View.GONE
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