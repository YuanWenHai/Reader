package com.will.reader.base

import androidx.fragment.app.DialogFragment

/**
 * created  by will on 2020/12/13 18:22
 */
open class BaseDialogFragment: DialogFragment() {

    override fun onResume() {
        val dialogWidth = resources.displayMetrics.widthPixels/5*4
        val dialogHeight = resources.displayMetrics.heightPixels/5*2
        dialog?.window?.setLayout(dialogWidth,dialogHeight)
        super.onResume()
    }

}