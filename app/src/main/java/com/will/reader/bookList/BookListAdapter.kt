package com.will.reader.bookList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.will.reader.R
import com.will.reader.bookList.viewmodel.BookItemViewModel
import com.will.reader.data.model.Book
import com.will.reader.databinding.ItemBookBinding

/**
 * created  by will on 2020/11/22 16:00
 */
class BookListAdapter: PagingDataAdapter<Book,BookListAdapter.ViewHolder>(DifferCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_book,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemBookBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(book: Book){
            val viewModel = BookItemViewModel(book.name,book.brief)
            binding.viewModel = viewModel
        }

    }
    class DifferCallback: DiffUtil.ItemCallback<Book>(){
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }
    }
}