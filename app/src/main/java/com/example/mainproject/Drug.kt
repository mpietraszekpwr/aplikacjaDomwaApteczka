package com.example.mainproject

import com.google.firebase.Timestamp

data class Drug(
    var name: String? = null,
    var uid: String? = null,
    var amount: Int = 0,
    var date: Timestamp? = null,
    var dose: String? = null
)
// recycleview usuwanie i edytowanie w kafelkach, plus opcja zmiana np koloru kafelka