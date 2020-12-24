package com.will.reader.reader

import android.content.DialogInterface
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
        val binding = FragmentSkipProgressBinding.inflate(inflater, container, false)
        initView(binding)
        return binding.root
    }
    private fun initView(binding: FragmentSkipProgressBinding){

        binding.fragmentSkipProgressButton.setOnClickListener {
            val text = binding.fragmentSkipProgressEdit.text.toString()
            val value = if(text.isNotBlank()) text.toFloat() else -1f
            val bundle = Bundle().also {
                it.putFloat(VALUE_KEY, value)
            }
            setFragmentResult(REQUEST_KEY, bundle)
            dismiss()
        }
    }



    //dialog有独立的phoneWindow实例，dialog的展示导致activity window失去焦点
    // 故在activity.window.decorView上设置的systemUI flag失效
    // 目前未发现合适的回调重新设置systemUI flag,所以暂时在dialog的dismiss lifecycle中重新在activity.window.decorView上设置flag
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val decorView = requireActivity().window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }


    companion object{
        const val REQUEST_KEY = "skip_progress_result"
        const val VALUE_KEY = "skip_value_key"
    }


}