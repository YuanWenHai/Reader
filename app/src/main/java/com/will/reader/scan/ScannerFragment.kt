package com.will.reader.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.will.reader.databinding.FragmentScannerBinding

/**
 * created  by will on 2020/11/22 16:59
 */
class ScannerFragment: Fragment(){
    private val viewModel: ScannerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentScannerBinding.inflate(inflater,container,false)
        init(binding)
        return binding.root
    }

    private fun init(binding: FragmentScannerBinding){
        val adapter = ScannerAdapter{
            viewModel.changeSelectState(it)
        }
        binding.fragmentScannerRecycler.adapter = adapter
        viewModel.getList().observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }
}