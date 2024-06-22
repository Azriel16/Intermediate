package com.dicoding.intermediate.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.intermediate.R
import com.dicoding.intermediate.data.remote.response.Story
import com.dicoding.intermediate.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val story = intent.getParcelableExtra(EXTRA_STORY, Story::class.java)
        if (story != null) {
            binding.apply {
                Glide.with(this@DetailStoryActivity)
                    .load(story.photoUrl)
                    .into(imageView2)
                textView.text = story.name
                textView2.text = story.description
            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
