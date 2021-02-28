package com.will.reader.chapterList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.will.reader.R
import com.will.reader.data.model.Chapter
import com.will.reader.databinding.ItemChapterBinding

class ChapterListAdapter(private val callback: (c: Chapter) -> Unit): ListAdapter<Chapter,ChapterListAdapter.ChapterVH>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterVH {
        return ChapterVH(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chapter,parent,false))
    }

    override fun onBindViewHolder(holder: ChapterVH, position: Int) {
        holder.bind(getItem(position))
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
    private class DiffCallback: DiffUtil.ItemCallback<Chapter>(){
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem.id == newItem.id
        }
    }
}