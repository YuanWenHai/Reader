package com.will.reader.chapterList

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.will.reader.R

/**
 * created  by will on 2020/12/12 18:21
 */
class ProgressBarDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       return AlertDialog.Builder(requireContext()).setView(R.layout.dialog_progress)
           .setTitle("正在索引..")
           .setMessage("正在索引")
           .create()
    }
}