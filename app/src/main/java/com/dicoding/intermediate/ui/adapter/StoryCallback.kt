package com.dicoding.intermediate.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.intermediate.data.remote.response.ListStoryItem

class StoryCallback(private val mOldStoryList: List<ListStoryItem>, private val mNewStoryList: List<ListStoryItem>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = mOldStoryList.size


    override fun getNewListSize(): Int = mNewStoryList.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = mOldStoryList[oldItemPosition].id == mNewStoryList[newItemPosition].id


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = mNewStoryList[newItemPosition] == mOldStoryList[oldItemPosition]

}