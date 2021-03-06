package com.will.reader.scan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.will.reader.R
import com.will.reader.base.BaseFragment
import com.will.reader.data.AppDataBase
import com.will.reader.data.BookRepository
import com.will.reader.databinding.FragmentScannerBinding
import com.will.reader.extensions.isBook
import com.will.reader.extensions.toPath
import com.will.reader.scan.viewmodel.ScannerViewModel
import com.will.reader.scan.viewmodel.ScannerViewModelFactory
import com.will.reader.util.makeLongToast
import java.io.File

/**
 * created  by will on 2020/11/22 16:59
 */
class ScannerFragment: BaseFragment(){
    private val viewModel: ScannerViewModel by viewModels{
        ScannerViewModelFactory(BookRepository.getInstance(AppDataBase.getInstance(requireContext()).getBookDao()))
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentScannerBinding.inflate(inflater,container,false)
        init(binding)
        return binding.root
    }

    private fun init(binding: FragmentScannerBinding){
        //toolbar
        setHasOptionsMenu(true)
        val parent = activity as AppCompatActivity
        parent.setSupportActionBar(binding.fragmentScannerToolbar)
        parent.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fragmentScannerToolbar.setNavigationOnClickListener{parent.onBackPressed()}
        //recycler
        val adapter = ScannerAdapter{
            viewModel.changeSelectState(it)
        }
        binding.fragmentScannerRecycler.adapter = adapter
        binding.fragmentScannerRecycler.addItemDecoration(DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL))
        //fab
        binding.fragmentScannerFab.setOnClickListener{
            viewModel.saveSelectedFile()
            findNavController().popBackStack()
        }

        viewModel.getList().observe(viewLifecycleOwner){
            list ->
            //在这版recyclerview依赖当中submitList()貌似有点问题，直接return了相同对象的提交，所以这里提交一个不同的
            adapter.submitList(list.toMutableList()) {
            }
        }
        viewModel.getCursor().observe(viewLifecycleOwner){
            if(it == FileScanner.CURSOR_FINISHED){
                binding.fragmentScannerCursor.visibility = View.GONE
            }else{
                binding.fragmentScannerCursor.text = ("搜索文件夹：$it")
            }
        }
        viewModel.hasPermission().observe(viewLifecycleOwner){
            binding.fragmentScannerNoPermissionMsg.visibility = if (it) View.GONE else View.VISIBLE
        }

        runWithStoragePermission({
            viewModel.setHasPermission(true)
            viewModel.scan()
         },
        {
            viewModel.setHasPermission(false)
        }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.scanner,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private val pickFileCode = 32767
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_scanner_open){
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "text/plain"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, pickFileCode)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == pickFileCode){
            if(resultCode == Activity.RESULT_OK){
                val path = data?.data?.toPath(requireContext()) ?: ""
                if(path.isNotEmpty()){
                    val file = File(path)
                    if(!file.exists()){
                        makeLongToast(requireContext(),"添加失败，文件 ${file.path}不存在")
                        return
                    }
                    if(!file.isBook()){
                        makeLongToast(requireContext(),"添加失败，文件 ${file.path}不是txt文件")
                        return
                    }
                    viewModel.save(file)
                    findNavController().popBackStack()
                }else{
                    makeLongToast(requireContext(),"添加失败，文件读取错误")
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}