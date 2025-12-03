package com.example.studymateplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Views
        val emailField = findViewById<EditText>(R.id.usernameField) // treat as email
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        // Animation
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        emailField.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) v.startAnimation(fadeIn) }
        passwordField.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) v.startAnimation(fadeIn) }

        // =======================================
        // ⭐ CREATE ACCOUNT NAVIGATION
        // =======================================
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // =======================================
        // ⭐ LOGIN BUTTON
        // =======================================
        btnLogin.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            // Input validation
            if (email.isEmpty()) {
                emailField.error = "Email is required"
                emailField.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.error = "Invalid email format"
                emailField.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordField.error = "Password is required"
                passwordField.requestFocus()
                return@setOnClickListener
            }

            // Firebase login
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val userId = currentUser.uid
                        // Fetch user info from Firestore
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { doc ->
                                if (doc.exists()) {
                                    val fullName = doc.getString("fullName") ?: "User"
                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.putExtra("USER_NAME", fullName)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "User data not found", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed to fetch user info: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Login failed", e)
                    Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
