package com.example.finaltest.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finaltest.R
import com.example.finaltest.dataclass.FamousPeople

class FamousPeopleAdapter(private val context: Context, private val famousPeople: List<FamousPeople>) : RecyclerView.Adapter<FamousPeopleAdapter.ViewHolder>() {
    var famousPeopleUpdateCallback: ((FamousPeople) -> Unit)? = null
    var onDeleteClickListener: ((FamousPeople) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val nationalityTextView: TextView = itemView.findViewById(R.id.nationalityTextView)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
        val peoplePoster: ImageView = itemView.findViewById(R.id.imgURL)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_famous_people, parent, false)
        return ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val famPeople = famousPeople[position]
        holder.nameTextView.text = famPeople.name
        holder.nationalityTextView.text = famPeople.nationality
        Glide.with(context)
            .load(famPeople.imageURL)
            .into(holder.peoplePoster)
        holder.buttonDelete.setOnClickListener {
            onDeleteClickListener?.invoke(famPeople)
        }
        //Set the background color of ratingTextView based on the rating
        val context = holder.itemView.context
        holder.constraintLayout.setOnClickListener {
            famousPeopleUpdateCallback?.invoke(famPeople)
        }
    }
    override fun getItemCount(): Int {
        return famousPeople.size
    }
}