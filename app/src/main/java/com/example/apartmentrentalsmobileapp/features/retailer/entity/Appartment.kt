package com.example.apartmentrentalsmobileapp.features.retailer.entity

import CachedApartment
import android.net.Uri

data class Apartment(
    var id: String = "",
    var imageUrl: String = "",
    val description: String = "",
    val areaSize: String = "",
    val rooms: String = "",
    val pricePerMonth: String = "",
    val ownerId: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

fun Apartment.toCached(): CachedApartment {
    return CachedApartment(
        id = id,
        imageUrl = imageUrl,
        description = description,
        areaSize = areaSize,
        rooms = rooms,
        pricePerMonth = pricePerMonth,
        ownerId = ownerId,
        lastUpdated = lastUpdated
    )
}

fun List<Apartment>.toCachedList(): List<CachedApartment> {
    return this.map { it.toCached() }
}