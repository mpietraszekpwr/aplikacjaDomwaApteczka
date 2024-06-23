package com.example.mainproject

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userDrugs: ArrayList<Drug>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DrugAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonGoToAdd = findViewById<Button>(R.id.przyciskDodawanieLeku)
        buttonGoToAdd.setOnClickListener {
            startActivity(Intent(this, AddMedicineActivity::class.java))
            finish()
        } //po kliknięciu wykonanie kodu przejsvie do aktywności addMedicineActivity

        recyclerView = findViewById(R.id.rv)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(this)



        firebaseAuth = FirebaseAuth.getInstance() //// Uzyskanie instancji FirebaseAuth
        val firestore = Firebase.firestore
        val uid = firebaseAuth.currentUser?.uid
        val drugsRef = firestore.collection("drugs")

        GlobalScope.launch(Dispatchers.IO) {
            val queryResult = drugsRef.whereEqualTo("uid", uid).get().await().toObjects(Drug::class.java)
            withContext(Dispatchers.Main){
                userDrugs= queryResult as ArrayList<Drug>
                for(drug in userDrugs){
                    if (drug.amount == 2){
                        sendNotification(drug.name!!)
                    }
                }
                adapter = DrugAdapter(userDrugs)
                adapter.onTileClick={drug -> //nadajemy nazwe arg zamiast defaultowego
                    var intent = Intent(baseContext, DetailsActivity::class.java)
                    intent.putExtra("uid", drug.uid)
                    intent.putExtra("name", drug.name)
                    intent.putExtra("date", drug.date)
                    intent.putExtra("dose", drug.dose)
                    intent.putExtra("amount", drug.amount)
                    startActivity(intent)
                    finish()
                }
                recyclerView.adapter=adapter
                Log.d("Data loaded", userDrugs.toString())
            }
        }
    }

    private fun createNotificationChannel(){ //https://developer.android.com/develop/ui/views/notifications/build-notification?fbclid=IwAR0Fwk-SMbWQB8Xu3IqYGiZC33Yme_lzYYnRu9T1dr8fR1BJYQKkki55KfM&hl=pl
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "Drug Amount Reminder"
            val channelDescription = "Kanał do przypominania o końcu zapasu leków"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ch1", channelName, importance)
            channel.description = channelDescription
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(drugname: String){
        val builder = NotificationCompat.Builder(this, "ch1")
            .setSmallIcon(R.drawable.pills_solid)
            .setContentTitle("${drugname} kończy się")
            .setContentText("Zostały tylko 2 tableki w opakowaniu. uzupełnij domową apteczkę.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(1,builder.build())
    }
}