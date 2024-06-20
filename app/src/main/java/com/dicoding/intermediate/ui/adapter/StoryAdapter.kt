package com.dicoding.intermediate.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.intermediate.data.remote.response.ListStoryItem
import com.dicoding.intermediate.databinding.StoryListBinding

class StoryAdapter(private var stories: List<ListStoryItem>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    fun updateStories(newStories: List<ListStoryItem>) {
        stories = newStories
        notifyDataSetChanged()
    }

    class StoryViewHolder(private val binding: StoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivPhotoDetail)
            binding.tvStoryName.text = story.name
            binding.tvDetailDescription.text = story.description
        }
    }
}
