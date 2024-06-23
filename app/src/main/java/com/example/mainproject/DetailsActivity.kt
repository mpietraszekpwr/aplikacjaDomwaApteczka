package com.example.mainproject

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val extras = intent.extras
        if (extras == null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            val uid = extras.getString("uid")
            val name  = extras.getString("name")
            val amount = extras.getInt("amount")
            val dose = extras.getString("dose")
            val date = extras.getParcelable("date", Timestamp::class.java)

            val fieldName = findViewById<TextView>(R.id.detalNazwaLeku)
            val fieldDose = findViewById<TextView>(R.id.detalDawka)
            val fieldAmount = findViewById<TextView>(R.id.detalIlosc)
            val fieldDate = findViewById<TextView>(R.id.detalDataWaznosci)
            val fieldAmountToSubtract = findViewById<EditText>(R.id.detalPoleIlosc)
            val buttonDelete = findViewById<Button>(R.id.detalUsun)
            val buttonSubtract = findViewById<Button>(R.id.detalZuzytyLek)

            fieldName.text= name
            fieldDose.text = dose
            fieldAmount.text = amount.toString()

            val dateformat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())// odczytanie daty dla strefy czasowej w ktorej jest tel
            fieldDate.text = dateformat.format(date!!.toDate())

            buttonDelete.setOnClickListener {
                val db = Firebase.firestore
                db.collection("drugs")
                    .whereEqualTo("uid", uid)
                    .whereEqualTo("amount", amount)
                    .whereEqualTo("name", name)
                    .whereEqualTo("dose", dose)
                    .whereEqualTo("date", date)
                    .get().addOnSuccessListener {documents ->
                        for(document in documents){
                            document.reference.delete().addOnSuccessListener {
                                Toast.makeText(this, "Usunięto lek", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish() //https://firebase.google.com/docs/firestore/query-data/get-data?fbclid=IwAR0L96tzrJbOHqUk7HcZp7gu-7Y5zGMUD0WlwcGMq_kRSUvjJMr-SonIqps&hl=pl#kotlin+ktx_3
                                //https://firebase.google.com/docs/firestore/manage-data/delete-data?fbclid=IwAR1snPhoPzaHNi3PuwaoJa5k6G-IywCHjtCEiwKNNXPk1QTvlwMopKckO6M&hl=pl
                                // https://firebase.google.com/docs/firestore/manage-data/delete-data?fbclid=IwAR04lwLS5MbNSXiHQVOoUEQbSKLHL3x8U8GYgGCqVLM30nAwxNNEGgG-5m8&hl=pl

                            }.addOnFailureListener {
                                Toast.makeText(this, "Usuwanie niepowiodło się", Toast.LENGTH_LONG).show()
                            }
                    }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Usuwanie niepowiodło się", Toast.LENGTH_LONG).show()
                    }
            }

            buttonSubtract.setOnClickListener {
                val amountToSubtract = Integer.parseInt(fieldAmountToSubtract.text.toString())
                var newAmount = amount - amountToSubtract
                if(newAmount < 0 ){
                    newAmount = 0
                }
                val db = Firebase.firestore
                db.collection("drugs")
                    .whereEqualTo("uid", uid)
                    .whereEqualTo("amount", amount)
                    .whereEqualTo("name", name)
                    .whereEqualTo("dose", dose)
                    .whereEqualTo("date", date)
                    .get().addOnSuccessListener {documents ->
                        for(document in documents){
                            document.reference.update("amount", newAmount).addOnSuccessListener {
                                Toast.makeText(this, "Zmieniono ilość", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish() //https://firebase.google.com/docs/firestore/query-data/get-data?fbclid=IwAR0L96tzrJbOHqUk7HcZp7gu-7Y5zGMUD0WlwcGMq_kRSUvjJMr-SonIqps&hl=pl#kotlin+ktx_3
                                //https://firebase.google.com/docs/firestore/manage-data/delete-data?fbclid=IwAR1snPhoPzaHNi3PuwaoJa5k6G-IywCHjtCEiwKNNXPk1QTvlwMopKckO6M&hl=pl
                                // https://firebase.google.com/docs/firestore/manage-data/delete-data?fbclid=IwAR04lwLS5MbNSXiHQVOoUEQbSKLHL3x8U8GYgGCqVLM30nAwxNNEGgG-5m8&hl=pl

                            }.addOnFailureListener {
                                Toast.makeText(this, "Modyfikacja ilości niepowiodła się", Toast.LENGTH_LONG).show()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Modyfikacja ilości niepowiodła się", Toast.LENGTH_LONG).show()
                    }
            }


        }
    }
}