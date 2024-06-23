package com.example.mainproject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddMedicineActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_medicine)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val medicineName = findViewById<EditText>(R.id.poleWpiszLek)
        val medicineAmount = findViewById<EditText>(R.id.poleIlosc)
        val medicineDose = findViewById<EditText>(R.id.poleDawkowanie)
        val medicineDate = findViewById<TextView>(R.id.dataWaznosci)
        val buttonAdd = findViewById<Button>(R.id.przyciskDodajLek)
        medicineAmount.setText("0")
        firebaseAuth = FirebaseAuth.getInstance()

        medicineDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                {
                    view, year, month, day ->
                        medicineDate.text = (day.toString()+"."+(month+1).toString()+"."+year.toString())
                },
                year, month, day
            )
            datePicker.show()
        }


        buttonAdd.setOnClickListener {
            val name = medicineName.text.toString()
            val ilosc = medicineAmount.text.toString().toInt()
            val dawka = medicineDose.text.toString()
            val data = medicineDate.text.toString()
            val formatter = SimpleDateFormat("dd.MM.yyyy")
            val dateObject = formatter.parse(data)
            val timestamp = dateObject?.let { it1 -> Timestamp(it1) }

            val drug = Drug(
                name,
                firebaseAuth.currentUser?.uid,
                ilosc,
                timestamp,
                dawka
            )
            val fireStore = Firebase.firestore
            fireStore.collection("drugs").add(drug).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()// po dodaniu leku powrót do mainaktivity
                }else{
                    Toast.makeText(this, "Nie udało się dodać leku", Toast.LENGTH_LONG).show()
                }


            }//wysyłka obiektu do bazy danych
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        super.onBackPressed() //orginalne zachowanie przycisku to wyłaczneie aplikacji, ale wczesniej otrzymuje polecenie cofania
    }
}