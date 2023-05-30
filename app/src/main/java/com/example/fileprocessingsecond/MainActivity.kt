package com.example.fileprocessingsecond

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fileprocessingsecond.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var _binding: ActivityMainBinding
    val binding
        get() = _binding

    private val openLauncher =
        registerForActivityResult(ActivityResultContracts
            .OpenDocument()) { uri ->
            try {
                uri?.let { openFile(it) }
            } catch (e: Exception){
                showError(MSG_ERROR_OPEN)
            }
    }

    private val saveLauncher =
        registerForActivityResult(ActivityResultContracts
            .CreateDocument("text/plain")){ uri ->
            try {
                uri?.let {saveFile(it)}
            } catch (e:Exception){
                showError(MSG_ERROR_SAVE)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            openButton.setOnClickListener { openLauncher.launch(arrayOf("text/plain")) }
            saveButton.setOnClickListener { saveLauncher.launch(FILE_NAME) }
        }
    }

    private fun openFile(uri: Uri) {
        val data = contentResolver.openInputStream(uri)?.use {
            String(it.readBytes())
        } ?: throw IllegalStateException("Can't open input stream")
        binding.contentEditText.setText(data)
    }

    private fun saveFile(uri: Uri) {
        contentResolver.openOutputStream(uri)?.use {
            val bytes = binding.contentEditText.text.toString().toByteArray()
            it.write(bytes)
        } ?: throw IllegalStateException("Can't open output stream")
    }

    private fun showError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val FILE_NAME = "myFile.txt"
        const val MSG_ERROR_SAVE = "cant save file"
        const val MSG_ERROR_OPEN = "cant open file"
    }
}