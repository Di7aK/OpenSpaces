package com.di7ak.openspaces.ui.features.scanner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.di7ak.openspaces.databinding.ActivityScannerBinding

class ScannerActivity : AppCompatActivity() {
    companion object {
        const val RESULT_TEXT = "text"
    }

    private lateinit var binding: ActivityScannerBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startScan()
    }

    private fun startScan() {
        codeScanner = CodeScanner(this, binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(RESULT_TEXT, it.text)
                })
                finish()
            }
        }
        codeScanner.startPreview()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}