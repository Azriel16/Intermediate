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
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.intermediate.R
import com.dicoding.intermediate.databinding.ActivityMainBinding
import com.dicoding.intermediate.ui.ViewModelFactory
import com.dicoding.intermediate.ui.adapter.StoryAdapter
import com.dicoding.intermediate.ui.login.LoginActivity
import com.dicoding.intermediate.ui.maps.MapsActivity
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

        lifecycleScope.launch {
            viewModel.stories.observe(this@MainActivity) { pagingData ->
                storyAdapter.submitData(lifecycle, pagingData)
            }
        }

        viewModel.newStory.observe(this) { newStory ->
            newStory?.let {
                storyAdapter.refresh()
                Toast.makeText(this, "New story uploaded!", Toast.LENGTH_SHORT).show()
            }
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
        storyAdapter = StoryAdapter()
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        storyAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvListStory.layoutManager?.scrollToPosition(0)
                }
            }
        })
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
            R.id.menu_map -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
