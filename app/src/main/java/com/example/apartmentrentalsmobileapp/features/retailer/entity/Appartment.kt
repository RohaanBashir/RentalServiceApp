package com.example.apartmentrentalsmobileapp.features.retailer.entity

import android.net.Uri

data class Apartment(
    var id: String = "",
    var imageUrl: String = "",
    val description: String = "",
    val areaSize: String = "",
    val rooms: String = "",
    val pricePerMonth: String = "",
    val ownerId: String = ""
)
