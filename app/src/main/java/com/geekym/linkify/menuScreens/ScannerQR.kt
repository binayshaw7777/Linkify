package com.geekym.linkify.menuScreens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.geekym.linkify.R
import com.geekym.linkify.databinding.FragmentGenerateQrBinding
import com.geekym.linkify.databinding.FragmentScannerQrBinding
import com.geekym.linkify.helper.Encryption
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ScannerQR : Fragment() {

    companion object {
        private const val PERMISSION_CAMERA_REQUEST = 1
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }


    lateinit var pvScan: androidx.camera.view.PreviewView
    lateinit var scanResultTextView: TextView
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { pvScan.display?.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    private var _binding: FragmentScannerQrBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentScannerQrBinding.inflate(inflater, container, false)

        scanResultTextView = binding.scanResultTextView
        pvScan = binding.scanPreview

        setupCamera()

        return binding.root
    }

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraProviderFuture.addListener(
            {
                try {
                    cameraProvider = cameraProviderFuture.get()
                    if (isCameraPermissionGranted()) {
                        bindCameraUseCases()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf(Manifest.permission.CAMERA),
                                PERMISSION_CAMERA_REQUEST
                            )
                        }
                    }
                } catch (e: ExecutionException) {
                    // Handle any errors (including cancellation) here.
                    Log.e("QrScanViewModel", "Unhandled exception", e)
                } catch (e: InterruptedException) {
                    Log.e("QrScanViewModel", "Unhandled exception", e)
                }
            },
            ContextCompat.getMainExecutor(requireActivity())
        )
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider?.unbind(previewUseCase)
        }

        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(pvScan.display.rotation)
            .build()

        previewUseCase?.setSurfaceProvider(pvScan.surfaceProvider)

        try {
            cameraSelector?.let {
                cameraProvider?.bindToLifecycle(this, it, previewUseCase)
            }
        } catch (illegalStateException: IllegalStateException) {
        } catch (illegalArgumentException: IllegalArgumentException) {
        }
    }

    private fun bindAnalyseUseCase() {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider?.unbind(analysisUseCase)
        }

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(pvScan.display.rotation)
            .build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(cameraExecutor, { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        })

        try {
            cameraSelector?.let {
                cameraProvider?.bindToLifecycle(/* lifecycleOwner= */this,
                    it, analysisUseCase
                )
            }
        } catch (illegalStateException: IllegalStateException) {
        } catch (illegalArgumentException: IllegalArgumentException) {
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        if (imageProxy.image == null) return
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.getOrNull(0)
                barcode?.rawValue?.let { code ->
                    val encryption = Encryption.getDefault("Key", "Salt", ByteArray(16))
                    val decrypted = encryption.decryptOrNull(code)
                    scanResultTextView.text = decrypted
                }
            }
            .addOnFailureListener {

            }.addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                setupCamera()
            } else {

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean = this.let {
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
    } == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}