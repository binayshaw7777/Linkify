package com.geekym.linkify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.geekym.linkify.databinding.FragmentGenerateQrBinding

class GenerateQR : Fragment() {

    private var _binding: FragmentGenerateQrBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGenerateQrBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        super.onDestroyView()
        _binding = null
    }
}