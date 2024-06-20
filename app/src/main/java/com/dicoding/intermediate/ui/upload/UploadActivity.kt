package com.dicoding.intermediate.ui.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val uploadViewModel: UploadViewModel by viewModels {
        val apiService = ApiConfig.getApiService()
        ViewModelFactory(UserRepository.getInstance(UserPreference.getInstance(dataStore), apiService))
    }
    private var selectedImageUri: Uri? = null
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                handleImageResult(data)
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
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), 1)
            return false
        }
        return true
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForResult.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startForResult.launch(intent)
    }

    private fun handleImageResult(data: Intent) {
        val imageBitmap = data.extras?.get("data") as? Bitmap
        selectedImageUri = data.data
        binding.imageView3.setImageURI(selectedImageUri)
    }

    private fun uploadImageAndDescription() {
        val description = binding.descriptionEditText.text.toString().trim()
        if (selectedImageUri != null && description.isNotEmpty()) {
            val photo = Utils().uriToFile(selectedImageUri!!, this)
            val compressedFile = Utils().compressImage(photo)
            val descriptionRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
            val filePart = MultipartBody.Part.createFormData(
                "photo",
                compressedFile.name,
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), compressedFile)
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
