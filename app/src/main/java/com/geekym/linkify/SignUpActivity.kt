package com.geekym.linkify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.geekym.linkify.databinding.ActivityMainBinding
import com.geekym.linkify.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db : DatabaseReference
    private lateinit var fd : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initialization()

        binding.SignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        //Sign Up button onClick
        binding.createAccount.setOnClickListener {
            val name = binding.SignUpName.text.toString()
            val email = binding.SignUpEmail.text.toString()
            val pass = binding.SignUpPassword.text.toString()

            //Create user object
            val user = User(name, email)

            if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (pass.length > 8) {

                    //Pass the email and password to create account
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            //get the generated uid
                            val uid = firebaseAuth.currentUser?.uid

                            //add user data in the Realtime Database
                            db.child(uid!!).setValue(user).addOnCompleteListener { it1 ->
                                if (it1.isSuccessful)
                                    startActivity(Intent(this, SignInActivity::class.java))
                            }

                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "Password length must be greater than 8", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Please enter the details!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Function to initialize variables
    private fun initialization() {
        firebaseAuth = FirebaseAuth.getInstance()
        fd = FirebaseDatabase.getInstance()
        db = fd.getReference("Users")
    }
}