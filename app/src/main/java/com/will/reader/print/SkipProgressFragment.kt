package com.will.reader.print

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.will.reader.base.BaseDialogFragment
import com.will.reader.databinding.FragmentSkipProgressBinding

/**
 * created  by will on 2020/12/20 15:31
 */
class SkipProgressFragment: BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSkipProgressBinding.inflate(inflater,container,false)
        initView(binding)
        return binding.root
    }
    private fun initView(binding: FragmentSkipProgressBinding){
        binding.fragmentSkipProgressButton.setOnClickListener {
            val text = binding.fragmentSkipProgressEdit.text.toString()
            val value = if(text.isNotBlank()) text.toFloat() else -1f
            val bundle = Bundle().also {
                it.putFloat(VALUE_KEY,value)
            }
            setFragmentResult(REQUEST_KEY,bundle)
            dismiss()
        }
    }

    companion object{
        const val REQUEST_KEY = "skip_progress_result"
        const val VALUE_KEY = "skip_value_key"
    }

    override fun onResume() {
        super.onResume()
        // TODO: 2020/12/20 status bar
        dialog?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        //dialog?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}