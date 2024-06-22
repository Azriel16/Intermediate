package com.dicoding.intermediate.ui.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.intermediate.data.UserRepository
import com.dicoding.intermediate.data.pref.UserPreference
import com.dicoding.intermediate.data.pref.dataStore
import com.dicoding.intermediate.data.remote.retrofit.ApiConfig
import com.dicoding.intermediate.databinding.ActivityUploadBinding
import com.dicoding.intermediate.ui.Utils
import com.dicoding.intermediate.ui.ViewModelFactory
import com.dicoding.intermediate.ui.main.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val uploadViewModel: UploadViewModel by viewModels {
        val apiService = ApiConfig.getApiService()
        ViewModelFactory(UserRepository.getInstance(UserPreference.getInstance(dataStore), apiService))
    }
    private var selectedImageUri: Uri? = null
    private var currentPhotoPath: String? = null

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                if (currentPhotoPath != null) {
                    // Image captured from camera
                    val file = File(currentPhotoPath!!)
                    selectedImageUri = Uri.fromFile(file)
                    binding.imageView3.setImageURI(selectedImageUri)
                } else if (data.data != null) {
                    // Image selected from gallery
                    selectedImageUri = data.data
                    binding.imageView3.setImageURI(selectedImageUri)
                }
            } else {
                Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button2.setOnClickListener {
            if (checkAndRequestPermissions()) {
                openCamera()
            }
        }

        binding.button.setOnClickListener {
            if (checkAndRequestPermissions()) {
                openGallery()
            }
        }

        binding.uploadButton.setOnClickListener {
            uploadImageAndDescription()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        return if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), 1)
            false
        } else {
            true
        }
    }

    private fun openCamera() {
        val photoFile: File? = try {
            Utils().createCustomTempFile(this)
        } catch (ex: Exception) {
            Toast.makeText(this, "Error occurred while creating the File", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.dicoding.intermediate.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            startForResult.launch(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startForResult.launch(intent)
    }

    private fun uploadImageAndDescription() {
        val description = binding.descriptionEditText.text.toString().trim()
        if (selectedImageUri != null && description.isNotEmpty()) {
            val photo = Utils().uriToFile(selectedImageUri!!, this)
            val compressedFile = Utils().compressImage(photo)
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData(
                "photo",
                compressedFile.name,
                compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )

            lifecycleScope.launch {
                binding.progressBar.visibility = View.VISIBLE
                try {
                    val response = uploadViewModel.uploadImage(filePart, descriptionRequestBody, null, null)
                    Toast.makeText(this@UploadActivity, response.message, Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } catch (e: Exception) {
                    Toast.makeText(this@UploadActivity, e.message, Toast.LENGTH_SHORT).show()
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        } else {
            Toast.makeText(this, "Please select an image and enter a description", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
