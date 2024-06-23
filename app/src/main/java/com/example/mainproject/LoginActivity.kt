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

public class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val emailField = findViewById<EditText>(R.id.poleEmailLogowanie)
        val passwordField = findViewById<EditText>(R.id.poleHasloLogowanie)
        val buttonLogin = findViewById<Button>(R.id.przyciskLogowanie)
        val buttonGoToRegister = findViewById<Button>(R.id.przyciskPrzejdzDoRejestracji)

        firebaseAuth = FirebaseAuth.getInstance() //metoda zwracająca obiekt klasy firebaseauth obecnie wykorzystywany w aplikacji
        buttonLogin.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.length !=0 && password.length != 0){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        startActivity(Intent(this,MainActivity::class.java))//odpalam nową aktywnośc z intencji, aktywnośc bedzie oparta na klasie mainaktivity
                        finish()//koniec obecnej aktywności https://firebase.google.com/docs/auth/android/password-auth?hl=pl
                    }else{
                        Toast.makeText(this,"Logowanie nieudane", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        buttonGoToRegister.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }
    }
}