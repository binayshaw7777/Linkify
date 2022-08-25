package com.geekym.linkify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.geekym.linkify.databinding.ActivitySignInBinding
import com.geekym.linkify.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initialization()

        binding.createNew.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.SignInEmail.text.toString()
            val password = binding.SignInPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (password.length > 8) {

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (firebaseAuth.currentUser?.isEmailVerified!!) {
                                startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                val u = firebaseAuth.currentUser
                                u?.sendEmailVerification()
                                Toast.makeText(
                                    this,
                                    "Email Verification sent to your mail",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Password length must be greater than 8", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Please enter the details!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initialization() {
        firebaseAuth = FirebaseAuth.getInstance()
    }
}