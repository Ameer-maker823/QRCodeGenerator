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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var qrBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnGenerate.setOnClickListener { generateQr() }
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
}