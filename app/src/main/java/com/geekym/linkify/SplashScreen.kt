package com.geekym.linkify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        handler = Handler()
        handler.postDelayed({

            // Delay and Start Activity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        } , 1500) // here we're delaying to startActivity after 2 seconds
    }
}