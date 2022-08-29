package com.geekym.linkify.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.geekym.linkify.R
import com.geekym.linkify.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class Forgot_Password : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        binding.send.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString().trim()
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                } else
                    Toast.makeText(this, "Email failed to", Toast.LENGTH_SHORT).show()
            }
        }
    }
}