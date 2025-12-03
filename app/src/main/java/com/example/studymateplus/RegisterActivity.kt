package com.example.studymateplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Correct EditText IDs from XML
        val fullNameField = findViewById<EditText>(R.id.fullNameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val confirmPasswordField = findViewById<EditText>(R.id.confirmPasswordField)

        // Buttons
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val tvBack = findViewById<TextView>(R.id.tvBackToLogin)

        // ================================
        // REGISTER BUTTON CLICK
        // ================================
        btnRegister.setOnClickListener {
            Log.d("RegisterActivity", "Register button clicked")

            val nameText = fullNameField.text.toString().trim()
            val emailText = emailField.text.toString().trim()
            val passwordText = passwordField.text.toString().trim()
            val confirmPasswordText = confirmPasswordField.text.toString().trim()

            // Input validation
            when {
                nameText.isEmpty() -> {
                    fullNameField.error = "Full name is required"
                    fullNameField.requestFocus()
                    return@setOnClickListener
                }
                emailText.isEmpty() -> {
                    emailField.error = "Email is required"
                    emailField.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(emailText).matches() -> {
                    emailField.error = "Invalid email format"
                    emailField.requestFocus()
                    return@setOnClickListener
                }
                passwordText.isEmpty() -> {
                    passwordField.error = "Password is required"
                    passwordField.requestFocus()
                    return@setOnClickListener
                }
                passwordText.length < 6 -> {
                    passwordField.error = "Password must be at least 6 characters"
                    passwordField.requestFocus()
                    return@setOnClickListener
                }
                passwordText != confirmPasswordText -> {
                    confirmPasswordField.error = "Passwords do not match"
                    confirmPasswordField.requestFocus()
                    return@setOnClickListener
                }
            }

            // Create Firebase user
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = task.result?.user
                        if (currentUser != null) {
                            val userId = currentUser.uid
                            Log.d("RegisterActivity", "Firebase user created with UID: $userId")

                            // Save user data in Firestore
                            val userMap = hashMapOf(
                                "fullName" to nameText,
                                "email" to emailText
                            )

                            db.collection("users").document(userId).set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Registration successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("RegisterActivity", "User data saved in Firestore")

                                    // Redirect to login
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Failed to save user data: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.e("RegisterActivity", "Firestore save error", e)
                                }
                        } else {
                            Toast.makeText(this, "User registration failed.", Toast.LENGTH_LONG).show()
                            Log.e("RegisterActivity", "Firebase user is null")
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("RegisterActivity", "Firebase createUser error", task.exception)
                    }
                }
        }

        // ================================
        // BACK TO LOGIN CLICK
        // ================================
        tvBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
