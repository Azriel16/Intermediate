package com.dicoding.intermediate.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.intermediate.R
import com.dicoding.intermediate.data.remote.response.Story
import com.dicoding.intermediate.databinding.ActivityMainBinding
import com.dicoding.intermediate.ui.ViewModelFactory
import com.dicoding.intermediate.ui.adapter.StoryAdapter
import com.dicoding.intermediate.ui.login.LoginActivity
import com.dicoding.intermediate.ui.upload.UploadActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        setupView()
        setupRecyclerView()
        setupFloatingActionButton()

        viewModel.fetchStories()

        viewModel.stories.observe(this) { stories ->
            binding.progressBar.visibility = View.GONE
            val convertedStories = stories.map {
                Story(
                    photoUrl = it.photoUrl,
                    createdAt = it.createdAt,
                    name = it.name,
                    description = it.description,
                    lon = it.lon as? Double,
                    id = it.id,
                    lat = it.lat as? Double
                )
            }
            storyAdapter.updateStories(convertedStories)
        }

        viewModel.error.observe(this) { errorMessage ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(emptyList())
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        binding.rvListStory.adapter = storyAdapter
    }

    private fun setupFloatingActionButton() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                lifecycleScope.launch {
                    viewModel.logout()
                    Toast.makeText(this@MainActivity, getString(R.string.logout), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
