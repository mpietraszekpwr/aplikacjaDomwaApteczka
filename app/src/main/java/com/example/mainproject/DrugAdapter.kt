package com.example.mainproject
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class DrugAdapter(private val mList: ArrayList<Drug>) : RecyclerView.Adapter<DrugAdapter.DrugViewHolder>() {
    var onTileClick:((Drug)->Unit)?=null // tworzy pole klasy które przyjmuje funkcje operującą na obiekcie klasy Drug(z wybranego kafelka przechodzi do detali wybranego kafla po kliknieciu )
    class DrugViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.poleNazwaLeku)
        val dose = itemView.findViewById<TextView>(R.id.poleInfoDawka)
        val amount = itemView.findViewById<TextView>(R.id.poleInfoIlosc)
        val date  = itemView.findViewById<TextView>(R.id.poleInfoData)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugViewHolder {
        val listView = LayoutInflater.from(parent.context).inflate(R.layout.drug_tile, parent,false)
        return DrugViewHolder(listView)


    }

    override fun onBindViewHolder(holder: DrugAdapter.DrugViewHolder, position: Int) {
        val drug = mList[position]
        holder.name.text=drug.name
        holder.amount.text=drug.amount.toString()
        val dateformat = SimpleDateFormat("dd.MM.yyyy")
        holder.date.text=dateformat.format(drug.date?.toDate()!!)
        holder.dose.text=drug.dose

        holder.itemView.setOnClickListener {
            onTileClick?.invoke(drug) // po kliknieciu na kafelek wywola metode ontileClick na danych z kafla na który kliknięto
        }


        when {
            drug.amount <= 20 -> holder.itemView.setBackgroundColor(Color.RED)
            drug.amount in 21..50 -> holder.itemView.setBackgroundColor(Color.GREEN)
            drug.amount > 50 -> holder.itemView.setBackgroundColor(Color.BLUE)
            else -> holder.itemView.setBackgroundColor(Color.YELLOW)
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


}