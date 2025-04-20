package com.example.apartmentrentalsmobileapp.features.retailer.views

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

class ApartmentAdapter(
    private val onEditClick: (Apartment) -> Unit,
    private val onDeleteClick: (Apartment) -> Unit
) : ListAdapter<Apartment, ApartmentAdapter.ApartmentViewHolder>(ApartmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApartmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return ApartmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApartmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ApartmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.propertyImage)
        private val desc: TextView = itemView.findViewById(R.id.propertyDescription)
        private val area: TextView = itemView.findViewById(R.id.propertyArea)
        private val rooms: TextView = itemView.findViewById(R.id.propertyRooms)
        private val price: TextView = itemView.findViewById(R.id.propertyPrice)


        @SuppressLint("SetTextI18n")
        fun bind(apartment: Apartment) {
            desc.text  = apartment.description
            area.text  = "Area: ${apartment.areaSize} sqft"
            rooms.text = "Rooms: ${apartment.rooms}"
            price.text = "$${apartment.pricePerMonth}/month"
            image.setImageURI(apartment.imageUrl.toUri())

            itemView.findViewById<View>(R.id.editButton).setOnClickListener {
                onEditClick(apartment)
            }
            itemView.findViewById<View>(R.id.deleteButton).setOnClickListener {
                onDeleteClick(apartment)
            }
        }
    }
    class ApartmentDiffCallback : DiffUtil.ItemCallback<Apartment>() {
        override fun areItemsTheSame(oldItem: Apartment, newItem: Apartment) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Apartment, newItem: Apartment) =
            oldItem == newItem
    }
}