package com.group4.qrcodegenerator

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.group4.qrcodegenerator.databinding.ActivityMainBinding
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var qrBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extra = (24 * resources.displayMetrics.density).toInt()
            v.setPadding(
                systemBars.left + extra,
                systemBars.top + extra,
                systemBars.right + extra,
                systemBars.bottom + extra
            )
            insets
        }

        binding.btnGenerate.setOnClickListener { generateQr() }
        binding.btnSave.setOnClickListener { qrBitmap?.let { saveToGallery(it) } }
        binding.btnShare.setOnClickListener { qrBitmap?.let { shareQr(it) } }
    }

    private fun generateQr() {
        val text = binding.etInput.text.toString().trim()
        if (text.isEmpty()) {
            binding.etInput.error = "Please enter text or a URL"
            return
        }
        try {
            qrBitmap = BarcodeEncoder().encodeBitmap(text, BarcodeFormat.QR_CODE, 800, 800)
            binding.ivQrCode.setImageBitmap(qrBitmap)
            binding.ivQrCode.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE
            binding.btnShare.visibility = View.VISIBLE
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToGallery(bitmap: Bitmap) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "QR_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRGenerator")
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            contentResolver.openOutputStream(uri)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            Toast.makeText(this, "Saved to Pictures/QRGenerator", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareQr(bitmap: Bitmap) {
        val dir = File(cacheDir, "qr").apply { mkdirs() }
        val file = File(dir, "qr_code.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share QR Code"))
    }
}