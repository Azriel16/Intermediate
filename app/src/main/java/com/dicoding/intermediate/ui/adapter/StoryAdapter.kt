package com.dicoding.intermediate.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair as UtilPair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.intermediate.data.remote.response.ListStoryItem
import com.dicoding.intermediate.databinding.StoryListBinding
import com.dicoding.intermediate.ui.detail.DetailStoryActivity
import android.os.Parcelable

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class StoryViewHolder(private val binding: StoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivPhotoDetail)
            binding.tvStoryName.text = story.name
            binding.tvDetailDescription.text = story.description

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.EXTRA_STORY, story as Parcelable) // Cast to Parcelable
                }

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context as Activity,
                    UtilPair(binding.ivPhotoDetail, "profile"),
                    UtilPair(binding.tvStoryName, "judul"),
                    UtilPair(binding.tvDetailDescription, "description")
                )

                context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
