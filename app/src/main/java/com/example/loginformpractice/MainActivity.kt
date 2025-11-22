package com.example.loginformpractice

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get EditTexts
        val usernameField = findViewById<EditText>(R.id.Username)
        val passwordField = findViewById<EditText>(R.id.Password)

        // Fade animation
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Username glow on focus
        usernameField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) view.startAnimation(fadeIn)
        }

        // Password glow on focus
        passwordField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) view.startAnimation(fadeIn)
        }
    }
}
