package com.will.reader.base

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * created  by will on 2020/12/13 17:15
 */
open class BaseFragment: Fragment() {
    private val STORAGE_PERMISSION_REQUEST_CODE = 668
    private var storageGrantCallback: (() -> Unit)? = null
    private var storageDenyCallback: (() -> Unit)? = null
    fun requestStoragePermission(grant: () -> Unit,deny: () -> Unit){
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        storageDenyCallback = deny
        storageGrantCallback = grant
        requestPermissions(arrayOf(permission),STORAGE_PERMISSION_REQUEST_CODE)
    }
    fun checkStoragePermission(): Boolean{
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        return  ContextCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == STORAGE_PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                storageGrantCallback?.let { it() }
            }else{
                storageDenyCallback?.let { it() }
            }
            storageGrantCallback = null
            storageDenyCallback = null
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}