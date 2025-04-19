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

class UserApartmentAdapter(
) : ListAdapter<Apartment, UserApartmentAdapter.ApartmentViewHolder>(ApartmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApartmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property_regular_user, parent, false)
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
            desc.text = apartment.description
            area.text = "Area: ${apartment.areaSize}"
            rooms.text = "Rooms: ${apartment.rooms}"
            price.text = "$${apartment.pricePerMonth}/month"

            try {
                image.setImageURI(apartment.imageUrl.toUri())
            } catch (e: Exception) {
                image.setImageResource(R.drawable.baseline_assignment_late_24) // Fallback image
            }

        }
    }

    class ApartmentDiffCallback : DiffUtil.ItemCallback<Apartment>() {
        override fun areItemsTheSame(oldItem: Apartment, newItem: Apartment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Apartment, newItem: Apartment): Boolean {
            return oldItem == newItem
        }
    }
}
