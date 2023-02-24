package com.example.camqr

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.camqr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: ActivityMainBinding
    private lateinit var scannerView: CodeScannerView
    private lateinit var textStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Lifecycle: onCreate()")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scannerView = binding.scannerView
        textStatus = binding.textStatus

        setupCameraPermissions()
        initCodeScanner()
    }

    private fun initCodeScanner() {
        codeScanner = CodeScanner(this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.TWO_DIMENSIONAL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    textStatus.text = getString(R.string.status_scanned)
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    textStatus.text = getString(R.string.status_error)
                }
                Log.e(TAG, "Camera initialization error: ${it.message}", it)
            }
        }

        codeScanner.startPreview()

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun setupCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need camera permission to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // permission granted
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Lifecycle: onResume()")
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.stopPreview()
        codeScanner.releaseResources()
        super.onPause()
        Log.d(TAG, "Lifecycle: onPause()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Lifecycle: onDestroy()")
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val CAMERA_REQUEST_CODE = 101

    }
}