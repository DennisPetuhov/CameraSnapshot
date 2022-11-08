package com.example.camerasnapshot


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.camerasnapshot.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



class MainActivity : AppCompatActivity() {


    private val myContentFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture())
        { isSucces ->
            if (isSucces) {
                latestTemproaryUri?.let {
                    previewImage.setImageURI(it)
                }
            }
        }
    private val myContentFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                previewImage.setImageURI(it)
            }

        }


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonCapturePhoto.setOnClickListener {
            takeImage()
        }
        binding.buttonOpenGallery.setOnClickListener {
            openImage()
        }

    }

    private fun openImage() {
        myContentFromGallery.launch("image/*")
    }




    fun takeImage() {
        lifecycleScope.launch {
            getTemproaryFile().let {
                latestTemproaryUri = it
                myContentFromCamera.launch(it)

            }
        }

    }

    private val previewImage by lazy { binding.ivImage }


    private var latestTemproaryUri: Uri? = null

    fun getTemproaryFile(): Uri {
        val timestamp: String = SimpleDateFormat(
            "yyyyMMdd-HHmmss-",
            Locale.US
        ).format(
            Date()
        )
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val temproaryFile = File.createTempFile(
            "PHOTO_${timestamp}",
            "jpg",

            storageDirectory
        ).apply {
            createNewFile()
            deleteOnExit()


        }
        return FileProvider.getUriForFile(
            this,
            "com.example.camerasnapshot.fileprovider",
            temproaryFile
        )
    }


}