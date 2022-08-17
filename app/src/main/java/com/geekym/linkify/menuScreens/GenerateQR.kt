package com.geekym.linkify.menuScreens

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.geekym.linkify.helper.Encryption
import com.geekym.linkify.databinding.FragmentGenerateQrBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

class GenerateQR : Fragment() {

    private var _binding: FragmentGenerateQrBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGenerateQrBinding.inflate(inflater, container, false)

        val message = "binayshaw7777@gmail.com"

        val encryption = Encryption.getDefault("Key", "Salt", ByteArray(16))

        val encrypted = encryption.encryptOrNull(message)
        val decrypted = encryption.decryptOrNull(encrypted)

        binding.qr.setImageBitmap(generateQrCode(encrypted))

        return binding.root
    }

    @Throws(WriterException::class)
    fun generateQrCode(value: String): Bitmap {
        val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel>()
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        val qrCodeWriter = QRCodeWriter()
        val size = 512
        val bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565)
        for (x in 0 until width)
            for (y in 0 until width)
                bmp.setPixel(y, x, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)

        return bmp
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}