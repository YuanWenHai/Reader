package com.will.reader.chapterList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.will.reader.R
import com.will.reader.data.model.Chapter
import com.will.reader.databinding.ItemChapterBinding

/**
 * created  by will on 2020/12/11 16:58
 */
class ChapterListAdapter(private val callback: (c: Chapter) -> Unit): PagingDataAdapter<Chapter,ChapterListAdapter.ChapterVH>(DiffCallback()) {


    override fun onBindViewHolder(holder: ChapterVH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterVH {
        return ChapterVH(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chapter,parent,false))
    }

    inner class ChapterVH(private val binding: ItemChapterBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
            getItem(absoluteAdapterPosition)?.let(callback)
            }
        }
        fun bind(chapter: Chapter){
            binding.itemChapterListName.text = chapter.name
        }

    }
    class DiffCallback: DiffUtil.ItemCallback<Chapter>(){
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem.id == newItem.id
        }
    }
}