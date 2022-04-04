package com.will.reader.bookList

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.will.reader.extensions.getRealName
import com.will.reader.extensions.isBook
import com.will.reader.extensions.toPath
import com.will.reader.util.makeLongToast
import com.will.reader.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * created  by will on 2020/11/22 12:15
 */
class BookListFragment : BaseFragment() {
    private val appViewModel: AppViewModel by activityViewModels()
    private val viewModel: BookListViewModel by viewModels {
        val appDb = AppDataBase.getInstance(requireContext())
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
        val binding = FragmentBookListBinding.inflate(inflater, container, false)
        init(binding)
        return binding.root
    }

    private fun init(binding: FragmentBookListBinding) {
        setHasOptionsMenu(true)
        val parent = activity as AppCompatActivity
        parent.setSupportActionBar(binding.bookListToolbar)
        val adapter = BookListAdapter { book ->
            if (viewModel.checkIfBookExists(book)) {
                appViewModel.updateBook(book)
                findNavController().navigate(BookListFragmentDirections.actionBookListFragmentToReaderFragment())
            } else {
                Toast.makeText(requireContext(), "该书籍已不存在！", Toast.LENGTH_LONG).show()
                viewModel.deleteBook(book)
            }
        }
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
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
                if (it.refresh is LoadState.NotLoading) {
                    binding.bookListEmptyView.visibility =
                        if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.book_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private val pickFileCode = 32767
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_book_list_add) {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "text/plain"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, pickFileCode)
            //findNavController().navigate(BookListFragmentDirections.actionBookListFragmentToScannerFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == pickFileCode && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        try{
                            val fileName = data.data.getRealName(requireContext()) ?: "${System.currentTimeMillis()}.txt"
                            val targetFile = File(requireContext().getExternalFilesDir("books"),fileName)
                            Files.copy(requireContext().contentResolver.openInputStream(data.data),Paths.get(targetFile.toString()))
                            if (!targetFile.exists()) {
                                makeLongToast(requireContext(), "添加失败，文件 ${targetFile.path}不存在")
                                return@withContext
                            }
                            if (!targetFile.isBook()) {
                                makeLongToast(requireContext(), "添加失败，文件 ${targetFile.path}不是文本文件")
                                return@withContext
                            }
                            viewModel.addBook(targetFile)
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }else{
                makeLongToast(requireContext(), "添加失败，文件读取错误")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}