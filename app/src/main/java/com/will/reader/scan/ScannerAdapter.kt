package com.will.reader.scan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.will.reader.R
import com.will.reader.databinding.FragmentScannerBinding
import com.will.reader.databinding.ItemScannerBinding

/**
 * created  by will on 2020/11/22 17:53
 */
class ScannerAdapter(private val callback: (index: Int) -> Unit): ListAdapter<FileItem,ScannerAdapter.ScannerViewHolder>(ScannerDifferCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannerViewHolder {
        return ScannerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_scanner,parent,false),callback)
    }

    override fun onBindViewHolder(holder: ScannerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScannerViewHolder(private val binding: ItemScannerBinding,private val callback: (index: Int) -> Unit): RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener{
                callback(bindingAdapterPosition)
            }
        }


        fun bind(item: FileItem){
            binding.item = item
            binding.notifyChange()
        }
    }

    class ScannerDifferCallback: DiffUtil.ItemCallback<FileItem>(){
        override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }
    }
}