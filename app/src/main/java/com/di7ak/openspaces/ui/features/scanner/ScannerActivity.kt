package com.di7ak.openspaces.ui.features.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.di7ak.openspaces.databinding.ActivityScannerBinding

class ScannerActivity : AppCompatActivity() {
    companion object {
        const val RESULT_TEXT = "text"
        private const val REQUEST_PERMISSIONS = 4
    }

    private lateinit var binding: ActivityScannerBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions()
        } else startScan()
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) -> {
                startScan()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSIONS)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startScan()
                }
            }

            else -> {
            }
        }
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