package com.will.reader.extensions

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

/**
 * created  by will on 2020/11/27 12:30
 */
fun Uri.toPath(context: Context): String?{

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, this)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(this)) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }

        } else if (isDownloadsDocument(this)) {
            val id = DocumentsContract.getDocumentId(this)
            val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(this)) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(this.scheme, ignoreCase = true)) {
        return getDataColumn(context, this, null, null)
    } else if ("file".equals(this.scheme, ignoreCase = true)) {
        return this.path
    }
    return null
}
fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    if(uri == null)
        return null
    try {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs,null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}