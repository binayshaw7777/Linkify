package com.geekym.linkify.menuScreens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.geekym.linkify.authentication.SignInActivity
import com.geekym.linkify.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserBinding.inflate(inflater, container, false)

        binding.logoutButton.setOnClickListener {
            logoutFun()
        }

        return binding.root
    }

    private fun logoutFun() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}