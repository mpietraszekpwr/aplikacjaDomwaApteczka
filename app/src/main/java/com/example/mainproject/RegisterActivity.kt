package com.example.mainproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailField = findViewById<EditText>(R.id.poleEmailRejestracja)
        val passwordField = findViewById<EditText>(R.id.poleHasloRejestracja)
        val buttonRegister = findViewById<Button>(R.id.przyciskRejestracja)
        val buttonSwitchToLogin = findViewById<Button>(R.id.przyciskPrzejdzDoLogowania)
        firebaseAuth = FirebaseAuth.getInstance()
        buttonRegister.setOnClickListener{
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.length != 0 && password.length != 0){
                if (checkPassword(password)){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(this,"Możesz się zalogować",Toast.LENGTH_LONG).show()
                            emailField.text.clear()
                            passwordField.text.clear() //https://firebase.google.com/docs/auth/android/password-auth?hl=pl
                        }else{
                            Toast.makeText(this,"Rejestracja nieudana",Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Hasło musi mieć dużą literę i cyfrę",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Podaj email i hasło",Toast.LENGTH_LONG).show()
            }
        }
        buttonSwitchToLogin.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
    private fun checkPassword(password: String): Boolean{
        var hasCapital = false
        var hasNumber = false

        for (letter in password){
            if (letter.isUpperCase()){
                hasCapital = true
            }
            val numeric = letter.toString().toIntOrNull()
            when(numeric){
                null -> {}
                else -> {hasNumber=true}
            }
        }
        return hasCapital && hasNumber
    }
}